package com.example.processingservice.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table("card")
@Data
public class Card {
    @Id
    private String id;
    private String status; // Ví dụ: PENDING, PROCESSED, FAILED
    private String processingRequestId; // Để liên kết với requestId của luồng xử lý
    private String cardNumber;

    public Card() {}
}