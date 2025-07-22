package com.example.processingservice.repository;

import com.example.processingservice.model.Pin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PinRepository extends ReactiveCrudRepository<Pin, String> {
}