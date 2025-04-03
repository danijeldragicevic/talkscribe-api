package com.productdock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the TalkscribeAPI application.
 */
@Slf4j
@SpringBootApplication
public class TalkscribeAPI {
    public static void main(String[] args) {
        SpringApplication.run(TalkscribeAPI.class, args);
        log.info("TalkscribeAPI started successfully.");
    }
}