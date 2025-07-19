package com.example.processingservice.model;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Data
public class Pin {
    @Id
    private String id;
    private String pinCode;
    private String status; // Ví dụ: PENDING, PROCESSED, FAILED
    private String processingRequestId; // Để liên kết với requestId của luồng xử lý

    public Pin(String string, String number, String pending, String string1) {
    }

    public Pin() {

    }
}