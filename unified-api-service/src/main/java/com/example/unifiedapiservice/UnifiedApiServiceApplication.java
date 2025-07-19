package com.example.unifiedapiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UnifiedApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnifiedApiServiceApplication.class, args);
    }

}