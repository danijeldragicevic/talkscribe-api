package com.productdock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Main class for the TalkscribeAPI application.
 */
@Slf4j
@Configuration
@EnableRetry
@EnableCaching
@SpringBootApplication
public class TalkscribeAPI {
    public static void main(String[] args) {
        SpringApplication.run(TalkscribeAPI.class, args);
        log.info("TalkscribeAPI started successfully.");
    }
}