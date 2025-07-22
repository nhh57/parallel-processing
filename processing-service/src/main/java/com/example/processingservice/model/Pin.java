package com.example.processingservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("pin")
@Data
public class Pin {
    @Id
    private String id;
    private String pinCode;
    private String status; // Ví dụ: PENDING, PROCESSED, FAILED
    private String processingRequestId; // Để liên kết với requestId của luồng xử lý


    public Pin() {

    }
}