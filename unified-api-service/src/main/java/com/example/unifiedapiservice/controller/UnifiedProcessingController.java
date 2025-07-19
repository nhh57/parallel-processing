package com.example.unifiedapiservice.controller;

import com.example.unifiedapiservice.dto.ProcessedItemResponse;
import com.example.unifiedapiservice.dto.UnifiedApiRequestDTO;
import com.example.unifiedapiservice.service.ItemStatusCheckerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC; // Import MDC
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api") // Base path cho API
public class UnifiedProcessingController {

    // Thêm @Async vào class hoặc vào phương thức nếu muốn xử lý bất đồng bộ
    // Nếu đặt ở đây, tất cả các phương thức trong controller sẽ mặc định là @Async
    // Nhưng tốt hơn nên đặt @Async trên phương thức cụ thể hoặc trong service layer

    private final ItemStatusCheckerService itemStatusCheckerService;

    public UnifiedProcessingController(ItemStatusCheckerService itemStatusCheckerService) {
        this.itemStatusCheckerService = itemStatusCheckerService;
    }

    @PostMapping("/check-status")
    @Async // Đánh dấu phương thức này sẽ được thực thi bất đồng bộ
    public CompletableFuture<ResponseEntity<ProcessedItemResponse>> checkStatus(
            @RequestBody UnifiedApiRequestDTO requestDTO,
            @RequestHeader(name = "X-Request-ID", required = false) String requestIdHeader) throws InterruptedException {

        // Đặt requestId vào MDC nếu nó được truyền qua header
        String currentRequestId = requestIdHeader;
        if (currentRequestId == null || currentRequestId.isEmpty()) {
            if (requestDTO.getRequestId() != null && !requestDTO.getRequestId().isEmpty()) {
                currentRequestId = requestDTO.getRequestId();
            } else {
                currentRequestId = java.util.UUID.randomUUID().toString();
            }
        }
        MDC.put("requestId", currentRequestId);
        log.info("Received request with requestId: {}", currentRequestId);

        log.info("Processing request for item type: {}", requestDTO.getType());

        // Gọi service bất đồng bộ và xử lý kết quả
        return itemStatusCheckerService.checkStatus(requestDTO)
                .thenApply(response -> {
                    log.info("Finished processing request for item type: {}. Response status: {}. RequestId: {}",
                            requestDTO.getType(), response.getStatus(), MDC.get("requestId"));
                    MDC.remove("requestId"); // Xóa requestId khỏi MDC trước khi trả về response
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    log.error("Error processing request for item type: {}. RequestId: {}. Error: {}",
                            requestDTO.getType(), MDC.get("requestId"), ex.getMessage());
                    MDC.remove("requestId"); // Xóa requestId khỏi MDC trong trường hợp lỗi
                    return ResponseEntity.status(500).body(null); // Trả về lỗi 500
                });
    }
}