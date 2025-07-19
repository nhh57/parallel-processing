# Task 5: Phát triển CardPinProcessingService (Đã hoàn thành, potential errors)

## Mục tiêu
Triển khai service chính `CardPinProcessingService` để xử lý danh sách hỗn hợp `Card` và `Pin` một cách song song, gọi API đến `Unified API Service`, và cập nhật cơ sở dữ liệu.

## Các bước thực hiện

- [x] Tạo lớp `CardPinProcessingService.java`:
    - [x] Tạo file `src/main/java/com/example/processingservice/service/CardPinProcessingService.java`.
    - [x] Inject `CardRepository`, `PinRepository`, và `ExternalApiClient`.
    - [x] Định nghĩa một `ExecutorService` để quản lý các luồng cho các tác vụ song song.

    ```java
    // src/main/java/com/example/processingservice/service/CardPinProcessingService.java
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
    import java.util.concurrent.Executors;
    import java.util.stream.Collectors;

    @Service
    public class CardPinProcessingService {

        private final CardRepository cardRepository;
        private final PinRepository pinRepository;
        private final ExternalApiClient externalApiClient;
        private final ExecutorService executorService;
        private final ObjectMapper objectMapper; // Để chuyển đổi Card/Pin sang Map

        public CardPinProcessingService(CardRepository cardRepository,
                                        PinRepository pinRepository,
                                        ExternalApiClient externalApiClient) {
            this.cardRepository = cardRepository;
            this.pinRepository = pinRepository;
            this.externalApiClient = externalApiClient;
            this.executorService = Executors.newFixedThreadPool(10); // Cấu hình số luồng phù hợp
            this.objectMapper = new ObjectMapper();
        }

        public void processMixedList(List<Object> mixedItems, String requestId) {
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

            // Xử lý Cards song song
            List<CompletableFuture<Void>> cardFutures = cards.stream()
                    .map(card -> CompletableFuture.supplyAsync(() -> processSingleCard(card, requestId), executorService)
                                .thenAccept(this::handleProcessingResponse)
                                .exceptionally(ex -> {
                                    System.err.println("Error processing card " + card.getId() + ": " + ex.getMessage());
                                    // Cập nhật trạng thái lỗi cho card trong DB nếu cần
                                    card.setStatus("FAILED_PROCESSING");
                                    cardRepository.save(card); // Lưu trạng thái lỗi
                                    return null;
                                }))
                    .collect(Collectors.toList());

            // Xử lý Pins song song
            List<CompletableFuture<Void>> pinFutures = pins.stream()
                    .map(pin -> CompletableFuture.supplyAsync(() -> processSinglePin(pin, requestId), executorService)
                                .thenAccept(this::handleProcessingResponse)
                                .exceptionally(ex -> {
                                    System.err.println("Error processing pin " + pin.getId() + ": " + ex.getMessage());
                                    // Cập nhật trạng thái lỗi cho pin trong DB nếu cần
                                    pin.setStatus("FAILED_PROCESSING");
                                    pinRepository.save(pin); // Lưu trạng thái lỗi
                                    return null;
                                }))
                    .collect(Collectors.toList());

            // Chờ tất cả các tác vụ hoàn thành
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    cardFutures.toArray(new CompletableFuture[0]),
                    pinFutures.toArray(new CompletableFuture[0])
            );

            // Có thể thêm logic sau khi tất cả hoàn thành, ví dụ: log tổng kết
            allFutures.thenRun(() -> System.out.println("All mixed items processing completed for requestId: " + requestId));
        }

        @Transactional
        private ProcessedItemResponse processSingleCard(Card card, String requestId) {
            System.out.println("Processing card: " + card.getCardNumber() + " with requestId: " + requestId);
            // Chuyển đổi Card object sang Map để gửi đi
            Map<String, Object> cardData = objectMapper.convertValue(card, Map.class);
            UnifiedApiRequestDTO requestDTO = new UnifiedApiRequestDTO(ItemType.CARD, cardData, requestId);
            return externalApiClient.callUnifiedApiService(requestDTO).block(); // block() để chờ kết quả
        }

        @Transactional
        private ProcessedItemResponse processSinglePin(Pin pin, String requestId) {
            System.out.println("Processing pin: " + pin.getPinCode() + " with requestId: " + requestId);
            // Chuyển đổi Pin object sang Map để gửi đi
            Map<String, Object> pinData = objectMapper.convertValue(pin, Map.class);
            UnifiedApiRequestDTO requestDTO = new UnifiedApiRequestDTO(ItemType.PIN, pinData, requestId);
            return externalApiClient.callUnifiedApiService(requestDTO).block(); // block() để chờ kết quả
        }

        @Transactional
        private void handleProcessingResponse(ProcessedItemResponse response) {
            if (response != null) {
                System.out.println("Received response for " + response.getType() + " with ID: " + response.getId() + ", Status: " + response.getStatus() + ", RequestId: " + response.getRequestId());
                if (response.getType() == ItemType.CARD) {
                    cardRepository.findById(Long.valueOf(response.getId())).ifPresent(card -> {
                        card.setStatus(response.getStatus());
                        cardRepository.save(card);
                    });
                } else if (response.getType() == ItemType.PIN) {
                    pinRepository.findById(Long.valueOf(response.getId())).ifPresent(pin -> {
                        pin.setStatus(response.getStatus());
                        pinRepository.save(pin);
                    });
                }
            } else {
                System.err.println("Received null response from Unified API Service.");
            }
        }
    }
    ```
    *Lưu ý:*
    *   `Executors.newFixedThreadPool(10)` là một ví dụ, bạn cần cấu hình số luồng phù hợp với tài nguyên và workload thực tế.
    *   `block()` được sử dụng ở đây để đơn giản hóa ví dụ, trong ứng dụng thực tế bạn nên xử lý `Mono` một cách non-blocking hoàn toàn (ví dụ: sử dụng `thenMany` hoặc `zip`). Tuy nhiên, với `CompletableFuture` ở đây, `block()` là cách đơn giản nhất để có kết quả đồng bộ từ `Mono` trong một `supplyAsync` đồng bộ.
    *   `@Transactional` được thêm vào các phương thức `processSingleCard`, `processSinglePin` và `handleProcessingResponse` để đảm bảo việc lưu DB diễn ra trong một transaction.