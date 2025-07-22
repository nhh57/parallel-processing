
package com.example.processingservice;

import com.example.processingservice.model.Card;
import com.example.processingservice.model.Pin;
import com.example.processingservice.service.CardPinProcessingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.processingservice.repository.CardRepository;
import com.example.processingservice.repository.PinRepository;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ProcessingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProcessingServiceApplication.class, args);
    }

    @Bean
    public WebClient.Builder getWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public CommandLineRunner run(CardPinProcessingService cardPinProcessingService,
                                 CardRepository cardRepository,
                                 PinRepository pinRepository) {
        return args -> {
            System.out.println("--- Starting data processing ---");

            // Fetch all Cards and Pins from the database
            Mono<List<Card>> cardsMono = cardRepository.findAll().collectList();
            Mono<List<Pin>> pinsMono = pinRepository.findAll().collectList();

            Mono.zip(cardsMono, pinsMono)
                .flatMap(tuple -> {
                    List<Card> cards = tuple.getT1();
                    List<Pin> pins = tuple.getT2();
                    return Flux.fromIterable(cards)
                               .cast(Object.class)
                               .mergeWith(Flux.fromIterable(pins).cast(Object.class))
                               .collectList()
                               .flatMap(mixedItems -> {
                                   // Generate a unique requestId for this batch
                                   String requestId = java.util.UUID.randomUUID().toString();
                                   System.out.println("Starting processing with RequestId: " + requestId);
                                   return cardPinProcessingService.processMixedList(mixedItems, requestId)
                                           .then(Mono.fromRunnable(() -> System.out.println("--- Data processing completed ---")));
                               });
                })
                .subscribe(
                    null, // onNext: not interested in the result of processMixedList Flux
                    error -> System.err.println("Error during processing: " + error.getMessage()), // onError
                    () -> System.out.println("All processing tasks initiated.") // onComplete
                );

        };
    }

}