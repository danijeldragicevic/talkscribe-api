package com.productdock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@Configuration
@EnableRetry
@EnableCaching
@EnableScheduling
@EnableAspectJAutoProxy
@SpringBootApplication
public class TalkscribeAPI {

    public static void main(String[] args) {
        SpringApplication.run(TalkscribeAPI.class, args);
        log.info("TalkscribeAPI started successfully.");
    }
}