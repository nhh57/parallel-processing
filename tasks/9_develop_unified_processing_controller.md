# Task 9: Phát triển UnifiedProcessingController

## Mục tiêu
Tạo REST Controller trong `Unified API Service` để nhận các yêu cầu xử lý từ `Processing Service`.

## Các bước thực hiện

- [ ] Tạo lớp `UnifiedProcessingController.java`:
    - [ ] Tạo file `src/main/java/com/example/unifiedapiservice/controller/UnifiedProcessingController.java`.
    - [ ] Sử dụng `@RestController` và `@RequestMapping`.
    - [ ] Định nghĩa endpoint `POST /check-status` để nhận `UnifiedApiRequestDTO`.
    - [ ] Inject `ItemStatusCheckerService` để ủy quyền xử lý.

    ```java
    // src/main/java/com/example/unifiedapiservice/controller/UnifiedProcessingController.java
    package com.example.unifiedapiservice.controller;

    import com.example.unifiedapiservice.dto.ProcessedItemResponse;
    import com.example.unifiedapiservice.dto.UnifiedApiRequestDTO;
    import com.example.unifiedapiservice.service.ItemStatusCheckerService;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.slf4j.MDC; // Import MDC
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestHeader;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/api") // Base path cho API
    public class UnifiedProcessingController {

        private static final Logger logger = LoggerFactory.getLogger(UnifiedProcessingController.class);
        private final ItemStatusCheckerService itemStatusCheckerService;

        public UnifiedProcessingController(ItemStatusCheckerService itemStatusCheckerService) {
            this.itemStatusCheckerService = itemStatusCheckerService;
        }

        @PostMapping("/check-status")
        public ResponseEntity<ProcessedItemResponse> checkStatus(
                @RequestBody UnifiedApiRequestDTO requestDTO,
                @RequestHeader(name = "X-Request-ID", required = false) String requestIdHeader) {

            // Đặt requestId vào MDC nếu nó được truyền qua header
            if (requestIdHeader != null && !requestIdHeader.isEmpty()) {
                MDC.put("requestId", requestIdHeader);
                logger.info("Received request with X-Request-ID: {}", requestIdHeader);
            } else if (requestDTO.getRequestId() != null && !requestDTO.getRequestId().isEmpty()) {
                MDC.put("requestId", requestDTO.getRequestId());
                logger.info("Received request with requestId in DTO: {}", requestDTO.getRequestId());
            } else {
                // Nếu không có requestId, tạo một cái mới cho mục đích logging nội bộ
                String newRequestId = java.util.UUID.randomUUID().toString();
                MDC.put("requestId", newRequestId);
                logger.info("Received request without external requestId. Generated new: {}", newRequestId);
            }

            logger.info("Processing request for item type: {}", requestDTO.getType());
            ProcessedItemResponse response = itemStatusCheckerService.checkStatus(requestDTO);
            logger.info("Finished processing request for item type: {}. Response status: {}", requestDTO.getType(), response.getStatus());

            MDC.remove("requestId"); // Xóa requestId khỏi MDC trước khi trả về response
            return ResponseEntity.ok(response);
        }
    }
    ```
    *Lưu ý:*
    *   Đoạn code trên đã bao gồm việc nhận `requestId` từ header `X-Request-ID` hoặc từ `requestDTO` và đặt vào MDC để phục vụ logging.
    *   `MDC.remove("requestId")` được gọi ở cuối để tránh `requestId` bị rò rỉ sang các luồng khác nếu thread được tái sử dụng.