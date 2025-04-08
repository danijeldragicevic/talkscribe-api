package com.productdock.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for CORS settings.
 */
@Configuration
public class CorsConfig {

    /**
     * Configures CORS settings for the application.
     * <p>
     * This method allows cross-origin requests from any origin to the "/api/**" endpoints
     * with GET and POST methods.
     *
     * @return a WebMvcConfigurer instance with CORS configuration.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/api/**")
                        //TODO Change to specific origins in production "https://my-app.s3.amazonaws.com"
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST");
            }
        };
    }
}