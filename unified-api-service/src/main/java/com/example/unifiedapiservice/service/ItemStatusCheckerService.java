package com.example.unifiedapiservice.service;
import java.util.concurrent.CompletableFuture;

import com.example.unifiedapiservice.dto.ItemType;
import com.example.unifiedapiservice.dto.ProcessedItemResponse;
import com.example.unifiedapiservice.dto.UnifiedApiRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID; // Dùng để tạo ID giả lập
@Slf4j
@Service
public class ItemStatusCheckerService {


    public CompletableFuture<ProcessedItemResponse> checkStatus(UnifiedApiRequestDTO requestDTO) {
        String requestId = requestDTO.getRequestId() != null ? requestDTO.getRequestId() : MDC.get("requestId");
        // Lưu requestId hiện tại để sử dụng trong luồng bất đồng bộ
        String currentRequestId = requestId;

        return CompletableFuture.supplyAsync(() -> {
            MDC.put("requestId", currentRequestId); // Đảm bảo requestId có trong MDC cho luồng bất đồng bộ

            log.info("Checking status for item type: {} with requestId: {}", requestDTO.getType(), requestId);

            Object idObject = requestDTO.getData().get("id");
            String itemId = (idObject != null) ? idObject.toString() : UUID.randomUUID().toString(); // Lấy ID hoặc tạo mới

            ProcessedItemResponse response = new ProcessedItemResponse();
            response.setId(itemId);
            response.setType(requestDTO.getType());
            response.setRequestId(requestId);

            // Logic kiểm tra trạng thái giả lập
            if (requestDTO.getType() == ItemType.CARD) {
                // Giả lập logic kiểm tra trạng thái thẻ
                Object cardNumberObject = requestDTO.getData().get("cardNumber");
                if (cardNumberObject != null && cardNumberObject.toString().startsWith("4")) {
                    response.setStatus("CR"); // Giả lập trạng thái "CR" cho thẻ bắt đầu bằng 4
                    response.setMessage("Card status checked: CR");
                } else {
                    response.setStatus("PROCESSED");
                    response.setMessage("Card status checked: PROCESSED");
                }
            } else if (requestDTO.getType() == ItemType.PIN) {
                // Giả lập logic kiểm tra trạng thái PIN
                Object pinCodeObject = requestDTO.getData().get("pinCode");
                if (pinCodeObject != null && pinCodeObject.toString().length() == 4) {
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
        }).whenComplete((result, throwable) -> {
            MDC.remove("requestId"); // Luôn xóa requestId khỏi MDC khi tác vụ hoàn thành
        });
    }
}