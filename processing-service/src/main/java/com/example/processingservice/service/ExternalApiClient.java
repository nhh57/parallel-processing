package com.example.processingservice.service;
import com.example.processingservice.dto.ProcessedItemResponse;
import com.example.processingservice.dto.UnifiedApiRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class ExternalApiClient {
    private final WebClient webClient;
    public ExternalApiClient(
            @Value("${unified.api.base-url}") String unifiedApiBaseUrl,
            @Value("${unified.api.connection-pool-size:500}") int connectionPoolSize,
            @Value("${unified.api.connect-timeout-millis:20000}") int connectTimeoutMillis,
            @Value("${unified.api.read-timeout-millis:30000}") int readTimeoutMillis,
            @Value("${unified.api.write-timeout-millis:30000}") int writeTimeoutMillis,
            @Value("${unified.api.max-idle-time-seconds:20}") int maxIdleTimeSeconds,
            @Value("${unified.api.max-life-time-seconds:300}") int maxLifeTimeSeconds,
            @Value("${unified.api.evict-in-background-seconds:30}") int evictInBackgroundSeconds,
            WebClient.Builder webClientBuilder) {
// Cấu hình ConnectionProvider với đầy đủ options
        ConnectionProvider connectionProvider = ConnectionProvider.builder("unified-api-connection-pool")
                .maxConnections(connectionPoolSize)
                .maxIdleTime(Duration.ofSeconds(maxIdleTimeSeconds))
                .maxLifeTime(Duration.ofSeconds(maxLifeTimeSeconds))
                .evictInBackground(Duration.ofSeconds(evictInBackgroundSeconds))
                .pendingAcquireTimeout(Duration.ofMillis(connectTimeoutMillis))
                .metrics(true) // Enable metrics cho monitoring
                .build();
// Cấu hình HttpClient với timeout handlers
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                .option(ChannelOption.SO_KEEPALIVE, true) // Keep connection alive
                .responseTimeout(Duration.ofMillis(readTimeoutMillis))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeoutMillis, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(writeTimeoutMillis, TimeUnit.MILLISECONDS))
                );
// Tạo WebClient
        this.webClient = webClientBuilder
                .baseUrl(unifiedApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        log.info("ExternalApiClient initialized with baseUrl: {}, connectionPoolSize: {}, connectTimeout: {}ms, readTimeout: {}ms",
                unifiedApiBaseUrl, connectionPoolSize, connectTimeoutMillis, readTimeoutMillis);
    }
    public Mono<ProcessedItemResponse> callUnifiedApiService(UnifiedApiRequestDTO requestDTO) {
        return webClient.post()
                .uri("/check-status")
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(ProcessedItemResponse.class)
// Retry mechanism với exponential backoff
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .maxBackoff(Duration.ofSeconds(5))
                        .filter(this::shouldRetry)
                        .doBeforeRetry(retrySignal ->
                                log.warn("Retrying call to Unified API. Attempt: {}, Error: {}",
                                        retrySignal.totalRetries() + 1,
                                        retrySignal.failure().getMessage())
                        )
                )
// Timeout cho toàn bộ operation (bao gồm retries)
                .timeout(Duration.ofSeconds(60))
// Enhanced error handling
                .doOnError(WebClientResponseException.class, this::handleWebClientException)
                .doOnError(Exception.class, this::handleGenericException)
                .onErrorMap(java.util.concurrent.TimeoutException.class,
                        ex -> new RuntimeException("Unified API call timed out after retries", ex));
    }
    /**
     * Xác định có nên retry hay không dựa trên loại exception
     */
    private boolean shouldRetry(Throwable throwable) {
        if (throwable instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) throwable;
// Retry cho 5xx errors và một số 4xx specific errors
            return ex.getStatusCode().is5xxServerError() ||
                    ex.getStatusCode().value() == 429 || // Too Many Requests
                    ex.getStatusCode().value() == 408; // Request Timeout
        }
// Retry cho network/connection issues
        return throwable instanceof java.net.ConnectException ||
                throwable instanceof java.net.SocketTimeoutException ||
                throwable instanceof java.util.concurrent.TimeoutException ||
                throwable instanceof io.netty.channel.ConnectTimeoutException;
    }
    /**
     * Xử lý WebClient response exceptions
     */
    private void handleWebClientException(WebClientResponseException ex) {
        if (ex.getStatusCode().is4xxClientError()) {
            log.error("Client error calling Unified API Service. Status: {}, Body: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
        } else if (ex.getStatusCode().is5xxServerError()) {
            log.error("Server error calling Unified API Service. Status: {}, Body: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
        } else {
            log.error("HTTP error calling Unified API Service. Status: {}, Body: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
        }
    }
    /**
     * Xử lý generic exceptions
     */
    private void handleGenericException(Exception ex) {
        log.error("Error calling Unified API Service: {}", ex.getMessage(), ex);
    }
    /**
     * Health check method
     */
    public Mono<String> healthCheck() {
        return webClient.get()
                .uri("/health")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorReturn("UNHEALTHY")
                .doOnSuccess(result -> log.debug("Health check result: {}", result))
                .doOnError(ex -> log.warn("Health check failed: {}", ex.getMessage()));
    }
}
