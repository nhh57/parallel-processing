package com.example.unifiedapiservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Value("${app.status-checker.thread-pool.size:8}")
    private int threadPoolSize;

    @Bean(name = "itemStatusCheckerExecutor")
    public Executor itemStatusCheckerExecutor() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}