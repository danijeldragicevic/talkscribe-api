package com.productdock.controller;

import com.productdock.model.SupportedLangauge;
import com.productdock.security.RateLimited;
import com.productdock.service.SupportedVoicesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/languages")
public class LanguageController {

    private final SupportedVoicesService supportedVoicesService;

    /**
     * Endpoint to get the list of supported languages for text-to-speech conversion.
     *
     * @return ResponseEntity containing the list of supported languages
     */
    @RateLimited(requests = 10, durationMinutes = 5)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SupportedLangauge>> getSupportedLanguages() {
        log.info("Received request to get supported languages");
        return ResponseEntity.ok(supportedVoicesService.getSupportedLanguages());
    }
}
