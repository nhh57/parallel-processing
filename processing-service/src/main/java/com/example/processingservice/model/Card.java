package com.example.processingservice.model;


import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
@Data
public class Card {
    @Id
    private String id;
    private String status; // Ví dụ: PENDING, PROCESSED, FAILED
    private String processingRequestId; // Để liên kết với requestId của luồng xử lý
    private String cardNumber;
    public Card(String id, String cardNumber, String status, String processingRequestId) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.status = status;
        this.processingRequestId = processingRequestId;
    }

    public Card() {}
}