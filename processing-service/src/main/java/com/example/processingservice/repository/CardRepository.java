package com.example.processingservice.repository;

import com.example.processingservice.model.Card;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends ReactiveCrudRepository<Card, String> {
}