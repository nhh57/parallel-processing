package com.example.unifiedapiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedApiRequestDTO {
    private ItemType type;
    private Map<String, Object> data; // Chứa dữ liệu của Card hoặc Pin
    private String requestId; // Request ID để theo dõi xuyên suốt
}