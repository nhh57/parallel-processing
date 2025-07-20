package com.example.unifiedapiservice.service;

import com.example.unifiedapiservice.dto.ItemType;
import com.example.unifiedapiservice.dto.ProcessedItemResponse;
import com.example.unifiedapiservice.dto.UnifiedApiRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class ItemStatusCheckerService {

    private final Executor itemStatusCheckerExecutor;

    public ItemStatusCheckerService(@Qualifier("itemStatusCheckerExecutor") Executor itemStatusCheckerExecutor) {
        this.itemStatusCheckerExecutor = itemStatusCheckerExecutor;
    }

    public CompletableFuture<ProcessedItemResponse> checkStatus(UnifiedApiRequestDTO requestDTO) {
        String requestId = requestDTO.getRequestId() != null ? requestDTO.getRequestId() : MDC.get("requestId");
        String currentRequestId = requestId;

        return CompletableFuture.supplyAsync(() -> {
            MDC.put("requestId", currentRequestId);

            log.info("Checking status for item type: {} with requestId: {}", requestDTO.getType(), requestId);

            Object idObject = requestDTO.getData().get("id");
            String itemId = (idObject != null) ? idObject.toString() : UUID.randomUUID().toString();

            ProcessedItemResponse response = new ProcessedItemResponse();
            response.setId(itemId);
            response.setType(requestDTO.getType());
            response.setRequestId(requestId);

            // Giả lập logic kiểm tra trạng thái
            if (requestDTO.getType() == ItemType.CARD) {
                Object cardNumberObject = requestDTO.getData().get("cardNumber");
                if (cardNumberObject != null && cardNumberObject.toString().startsWith("4")) {
                    response.setStatus("CR");
                    response.setMessage("Card status checked: CR");
                } else {
                    response.setStatus("PROCESSED");
                    response.setMessage("Card status checked: PROCESSED");
                }
            } else if (requestDTO.getType() == ItemType.PIN) {
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
        }, itemStatusCheckerExecutor).whenComplete((result, throwable) -> {
            MDC.remove("requestId");
        });
    }
}