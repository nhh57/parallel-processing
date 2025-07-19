
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            List<Object> mixedItems = Stream.concat(
                    cardRepository.findAll().stream(),
                    pinRepository.findAll().stream()
            ).collect(Collectors.toList());

            // Generate a unique requestId for this batch
            String requestId = java.util.UUID.randomUUID().toString();
            System.out.println("Starting processing with RequestId: " + requestId);
            cardPinProcessingService.processMixedList(mixedItems, requestId); // Wait for all async tasks to complete

            System.out.println("--- Data processing completed ---");
        };
    }

}