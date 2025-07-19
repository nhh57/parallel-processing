# Task 10: Phát triển ItemStatusCheckerService

## Mục tiêu
Triển khai service trong `Unified API Service` để kiểm tra trạng thái của các item (`Card` hoặc `Pin`) và trả về phản hồi.

## Các bước thực hiện

- [ ] Tạo lớp `ItemStatusCheckerService.java`:
    - [ ] Tạo file `src/main/java/com/example/unifiedapiservice/service/ItemStatusCheckerService.java`.
    - [ ] Sử dụng `@Service`.
    - [ ] Triển khai phương thức `checkStatus` để xử lý `UnifiedApiRequestDTO`, phân biệt `ItemType` và trả về `ProcessedItemResponse`.

    ```java
    // src/main/java/com/example/unifiedapiservice/service/ItemStatusCheckerService.java
    package com.example.unifiedapiservice.service;

    import com.example.unifiedapiservice.dto.ItemType;
    import com.example.unifiedapiservice.dto.ProcessedItemResponse;
    import com.example.unifiedapiservice.dto.UnifiedApiRequestDTO;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.slf4j.MDC;
    import org.springframework.stereotype.Service;

    import java.util.UUID; // Dùng để tạo ID giả lập

    @Service
    public class ItemStatusCheckerService {

        private static final Logger logger = LoggerFactory.getLogger(ItemStatusCheckerService.class);

        public ProcessedItemResponse checkStatus(UnifiedApiRequestDTO requestDTO) {
            String requestId = requestDTO.getRequestId() != null ? requestDTO.getRequestId() : MDC.get("requestId");
            MDC.put("requestId", requestId); // Đảm bảo requestId có trong MDC cho service này

            log.info("Checking status for item type: {} with requestId: {}", requestDTO.getType(), requestId);

            String itemId = requestDTO.getData().containsKey("id") ? requestDTO.getData().get("id").toString() : UUID.randomUUID().toString(); // Lấy ID hoặc tạo mới

            ProcessedItemResponse response = new ProcessedItemResponse();
            response.setId(itemId);
            response.setType(requestDTO.getType());
            response.setRequestId(requestId);

            // Logic kiểm tra trạng thái giả lập
            if (requestDTO.getType() == ItemType.CARD) {
                // Giả lập logic kiểm tra trạng thái thẻ
                if (requestDTO.getData().containsKey("cardNumber") && requestDTO.getData().get("cardNumber").toString().startsWith("4")) {
                    response.setStatus("CR"); // Giả lập trạng thái "CR" cho thẻ bắt đầu bằng 4
                    response.setMessage("Card status checked: CR");
                } else {
                    response.setStatus("PROCESSED");
                    response.setMessage("Card status checked: PROCESSED");
                }
            } else if (requestDTO.getType() == ItemType.PIN) {
                // Giả lập logic kiểm tra trạng thái PIN
                if (requestDTO.getData().containsKey("pinCode") && requestDTO.getData().get("pinCode").toString().length() == 4) {
                    response.setStatus("VALID");
                    response.setMessage("PIN status checked: VALID");
                } else {
                    response.setStatus("INVALID");
                    response.setMessage("PIN status checked: INVALID");
                }
            } else {
                response.setStatus("UNKNOWN_TYPE");
                response.setMessage("Unknown item type provided.");
            }

            log.info("Finished checking status for item type: {} with status: {}. RequestId: {}", requestDTO.getType(), response.getStatus(), requestId);
            return response;
        }
    }
    ```
    *Lưu ý:* Logic kiểm tra trạng thái ở đây chỉ là giả lập. Trong thực tế, service này có thể gọi đến các hệ thống bên ngoài, kiểm tra database, hoặc thực hiện các tính toán phức tạp để xác định trạng thái thực sự của item.