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
import jakarta.transaction.Transactional; // Để đảm bảo giao dịch DB

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;

@Service
public class CardPinProcessingService {

    private final CardRepository cardRepository;
    private final PinRepository pinRepository;
    private final ExternalApiClient externalApiClient;
    private final ExecutorService executorService;
    private final ObjectMapper objectMapper;

    public CardPinProcessingService(CardRepository cardRepository,
                                    PinRepository pinRepository,
                                    ExternalApiClient externalApiClient,
                                    @Value("${app.processing.thread-pool.core-size:50}") int corePoolSize,
                                    @Value("${app.processing.thread-pool.max-size:100}") int maxPoolSize,
                                    @Value("${app.processing.thread-pool.queue-capacity:5000}") int queueCapacity) {
        this.cardRepository = cardRepository;
        this.pinRepository = pinRepository;
        this.externalApiClient = externalApiClient;
        this.executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy() // Handle when queue is full
        );
        this.objectMapper = new ObjectMapper();
    }

    public void processMixedList(List<Object> mixedItems, String requestId) {
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

        // Process Cards in parallel
        List<CompletableFuture<Void>> cardFutures = cards.stream()
                .map(card -> processSingleCard(card, requestId)
                        .exceptionally(ex -> {
                            System.err.println("Error processing card " + card.getId() + ": " + ex.getMessage());
                            card.setStatus("FAILED_PROCESSING");
                            cardRepository.save(card);
                            return null;
                        }))
                .collect(Collectors.toList());

        List<CompletableFuture<Void>> pinFutures = pins.stream()
                .map(pin -> processSinglePin(pin, requestId)
                        .exceptionally(ex -> {
                            System.err.println("Error processing pin " + pin.getId() + ": " + ex.getMessage());
                            pin.setStatus("FAILED_PROCESSING");
                            pinRepository.save(pin);
                            return null;
                        }))
                .collect(Collectors.toList());

        // Combine all CompletableFutures and wait for all to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                CompletableFuture.allOf(cardFutures.toArray(new CompletableFuture[0])),
                CompletableFuture.allOf(pinFutures.toArray(new CompletableFuture[0]))
        );

        // Có thể thêm logic sau khi tất cả hoàn thành, ví dụ: log tổng kết
        allFutures.thenRun(() -> System.out.println("All mixed items processing completed for requestId: " + requestId));
    }

    @Transactional
    protected CompletableFuture<Void> processSingleCard(Card card, String requestId) {
        System.out.println("Processing card: " + card + " with requestId: " + requestId);
        Map<String, Object> cardData = objectMapper.convertValue(card, Map.class);
        System.out.println("Card Data being sent: " + cardData);
        UnifiedApiRequestDTO requestDTO = new UnifiedApiRequestDTO(ItemType.CARD, cardData, requestId);

        return externalApiClient.callUnifiedApiService(requestDTO)
                .doOnNext(this::handleProcessingResponse)
                .doOnError(e -> System.err.println("Error processing card " + card.getId() + ": " + e.getMessage()))
                .then()
                .toFuture();
    }

    @Transactional
    protected CompletableFuture<Void> processSinglePin(Pin pin, String requestId) {
        System.out.println("Processing pin: " + pin.getPinCode() + " with requestId: " + requestId);
        Map<String, Object> pinData = objectMapper.convertValue(pin, Map.class);
        System.out.println("Pin Data being sent: " + pinData);
        UnifiedApiRequestDTO requestDTO = new UnifiedApiRequestDTO(ItemType.PIN, pinData, requestId);

        return externalApiClient.callUnifiedApiService(requestDTO)
                .doOnNext(this::handleProcessingResponse)
                .doOnError(e -> System.err.println("Error processing pin " + pin.getId() + ": " + e.getMessage()))
                .then()
                .toFuture();
    }

    @Transactional
    protected void handleProcessingResponse(ProcessedItemResponse response) {
        if (response != null) {
            System.out.println("Received response for " + response.getType() + " with ID: " + response.getId() + ", Status: " + response.getStatus() + ", RequestId: " + response.getRequestId());
            if (response.getType() == ItemType.CARD) {
                cardRepository.findById(response.getId()).ifPresent(card -> {
                    card.setStatus(response.getStatus());
                    cardRepository.save(card);
                });
            } else if (response.getType() == ItemType.PIN) {
                pinRepository.findById(response.getId()).ifPresent(pin -> {
                    pin.setStatus(response.getStatus());
                    pinRepository.save(pin);
                });
            }
        } else {
            System.err.println("Received null response from Unified API Service.");
        }
    }
}