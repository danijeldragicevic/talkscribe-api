package com.productdock.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    /**
     * Configures CORS settings for the application.
     * <p>
     * This method allows cross-origin requests from the frontend app to the "/api/**" endpoints
     * with GET, POST and OPTIONS methods.
     *
     * @return a WebMvcConfigurer instance with CORS configuration.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/api/**")
                        //.allowedOrigins("*")
                        .allowedOrigins("https://talkscribe.org")
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}