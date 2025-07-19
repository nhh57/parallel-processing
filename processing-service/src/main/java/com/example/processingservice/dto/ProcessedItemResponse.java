package com.example.processingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedItemResponse {
    private String id; // ID của Card hoặc Pin đã xử lý
    private ItemType type;
    private String status; // Trạng thái xử lý (ví dụ: SUCCESS, FAILED, CR)
    private String message; // Thông báo chi tiết
    private String requestId; // Request ID để theo dõi xuyên suốt
}