package com.example.processingservice.service;

import com.example.processingservice.dto.ProcessedItemResponse;
import com.example.processingservice.dto.UnifiedApiRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
public class ExternalApiClient {

    private final WebClient webClient;
    private final Semaphore semaphore;

    public ExternalApiClient(@Value("${unified.api.base-url}") String unifiedApiBaseUrl,
                             @Value("${unified.api.connection-pool-size:200}") int connectionPoolSize,
                             @Value("${unified.api.connect-timeout-millis:5000}") int connectTimeoutMillis,
                             @Value("${unified.api.read-timeout-millis:30000}") int readTimeoutMillis,
                             @Value("${unified.api.concurrent-calls-limit:25}") int concurrentCallsLimit,
                             WebClient.Builder webClientBuilder) {

        HttpClient httpClient = HttpClient.create(ConnectionProvider.builder("unified-api-connection-pool")
                        .maxConnections(connectionPoolSize)
                        .pendingAcquireTimeout(Duration.ofMillis(connectTimeoutMillis))
                        .build())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                .responseTimeout(Duration.ofMillis(readTimeoutMillis));

        this.webClient = webClientBuilder.baseUrl(unifiedApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        this.semaphore = new Semaphore(concurrentCallsLimit);
    }

    public Mono<ProcessedItemResponse> callUnifiedApiService(UnifiedApiRequestDTO requestDTO) {
        return Mono.defer(() -> {
            try {
                semaphore.acquire(); // Acquire a permit
                return webClient.post()
                        .uri("/check-status") // Endpoint của Unified API Service
                        .bodyValue(requestDTO)
                        .retrieve()
                        .bodyToMono(ProcessedItemResponse.class)
                        .doFinally(signalType -> semaphore.release()) // Release the permit when the Mono completes
                        .doOnError(e -> System.err.println("Error calling Unified API Service: " + e.getMessage())); // Xử lý lỗi cơ bản
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Mono.error(new RuntimeException("Failed to acquire semaphore permit", e));
            }
        });
    }
}