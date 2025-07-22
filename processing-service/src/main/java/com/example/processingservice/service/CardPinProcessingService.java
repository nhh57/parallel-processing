package com.example.processingservice.service;

import com.example.processingservice.model.Card;
import com.example.processingservice.model.Pin;
import com.example.processingservice.repository.CardRepository;
import com.example.processingservice.repository.PinRepository;
import com.example.processingservice.dto.ItemType;
import com.example.processingservice.dto.ProcessedItemResponse;
import com.example.processingservice.dto.UnifiedApiRequestDTO;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper; // Để chuyển đổi Object sang Map
import lombok.extern.slf4j.Slf4j; // Import Slf4j
import org.springframework.transaction.annotation.Transactional; // Use Spring's Transactional for R2DBC

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j // Add Slf4j annotation
@Service
public class CardPinProcessingService {

    private final CardRepository cardRepository;
    private final PinRepository pinRepository;
    private final ExternalApiClient externalApiClient;
    private final ObjectMapper objectMapper;

    public CardPinProcessingService(CardRepository cardRepository,
                                    PinRepository pinRepository,
                                    ExternalApiClient externalApiClient) {
      this.cardRepository = cardRepository;
      this.pinRepository = pinRepository;
      this.externalApiClient = externalApiClient;
      this.objectMapper = new ObjectMapper();
  }

    public Flux<ProcessedItemResponse> processMixedList(List<Object> mixedItems, String requestId) {
        // Separate Cards and Pins into different lists
        List<Card> cards = mixedItems.stream()
                .filter(item -> item instanceof Card)
                .map(item -> (Card) item)
                .collect(Collectors.toList());

        List<Pin> pins = mixedItems.stream()
                .filter(item -> item instanceof Pin)
                .map(item -> (Pin) item)
                .collect(Collectors.toList());

        // Thiết lập requestId cho các item trước khi xử lý
        cards.forEach(card -> card.setProcessingRequestId(requestId));
        pins.forEach(pin -> pin.setProcessingRequestId(requestId));

        // Process Cards and Pins in parallel using Flux.merge
        Flux<ProcessedItemResponse> cardProcessingFlux = Flux.fromIterable(cards)
                .flatMap(card -> processSingleCard(card, requestId)
                        .onErrorResume(ex -> { // Use onErrorResume to provide a fallback Mono
                            log.error("Error processing card {}: {}", card.getId(), ex.getMessage(), ex);
                            card.setStatus("FAILED_PROCESSING");
                            // Save the card and return Mono.empty() to complete this specific item's stream
                            return cardRepository.save(card).then(Mono.empty());
                        }));

        Flux<ProcessedItemResponse> pinProcessingFlux = Flux.fromIterable(pins)
                .flatMap(pin -> processSinglePin(pin, requestId)
                        .onErrorResume(ex -> { // Use onErrorResume to provide a fallback Mono
                            log.error("Error processing pin {}: {}", pin.getId(), ex.getMessage(), ex);
                            pin.setStatus("FAILED_PROCESSING");
                            // Save the pin and return Mono.empty() to complete this specific item's stream
                            return pinRepository.save(pin).then(Mono.empty());
                        }));

        return Flux.merge(cardProcessingFlux, pinProcessingFlux)
                .doFinally(signalType -> log.info("All mixed items processing completed for requestId: {}", requestId));
    }
 
   protected Mono<ProcessedItemResponse> processSingleCard(Card card, String requestId) {
       log.info("Processing card: {} with requestId: {}", card, requestId);
       Map<String, Object> cardData = objectMapper.convertValue(card, Map.class);
       log.info("Card Data being sent: {}", cardData);
       UnifiedApiRequestDTO requestDTO = new UnifiedApiRequestDTO(ItemType.CARD, cardData, requestId);

       return externalApiClient.callUnifiedApiService(requestDTO)
               .flatMap(response -> handleProcessingResponse(response)); // Use flatMap for reactive chaining
   }

   protected Mono<ProcessedItemResponse> processSinglePin(Pin pin, String requestId) {
       log.info("Processing pin: {} with requestId: {}", pin.getPinCode(), requestId);
       Map<String, Object> pinData = objectMapper.convertValue(pin, Map.class);
       log.info("Pin Data being sent: {}", pinData);
       UnifiedApiRequestDTO requestDTO = new UnifiedApiRequestDTO(ItemType.PIN, pinData, requestId);

       return externalApiClient.callUnifiedApiService(requestDTO)
               .flatMap(response -> handleProcessingResponse(response)); // Use flatMap for reactive chaining
   }

   @Transactional
   protected Mono<ProcessedItemResponse> handleProcessingResponse(ProcessedItemResponse response) {
       if (response != null) {
           log.info("Received response for {} with ID: {}, Status: {}, RequestId: {}", response.getType(), response.getId(), response.getStatus(), response.getRequestId());
           if (response.getType() == ItemType.CARD) {
               return cardRepository.findById(response.getId())
                       .flatMap(card -> {
                           card.setStatus(response.getStatus());
                           return cardRepository.save(card);
                       })
                       .switchIfEmpty(Mono.error(new RuntimeException("Card not found for ID: " + response.getId())))
                       .thenReturn(response); // Return the original response
           } else if (response.getType() == ItemType.PIN) {
               return pinRepository.findById(response.getId())
                       .flatMap(pin -> {
                           pin.setStatus(response.getStatus());
                           return pinRepository.save(pin);
                       })
                       .switchIfEmpty(Mono.error(new RuntimeException("Pin not found for ID: " + response.getId())))
                       .thenReturn(response); // Return the original response
           }
       }
       log.error("Received null response or unhandled item type from Unified API Service.");
       return Mono.error(new RuntimeException("Received null response or unhandled item type."));
   }
}