package com.productdock.repository;

import com.productdock.exception.ComprehendRepositoryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import software.amazon.awssdk.services.comprehend.model.DetectDominantLanguageRequest;
import software.amazon.awssdk.services.comprehend.model.DetectDominantLanguageResponse;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ComprehendRepository {
    private final ComprehendClient comprehendClient;

    /**
     * Detects the dominant language of the given text using AWS Comprehend.
     * This method uses the default language code "en-US" if no languages are detected.
     *
     * @param text the text for which to detect the dominant language
     * @return the detected language code
     * @throws ComprehendRepositoryException if an error occurs during language detection
     */
    public String detectLanguage(String text)  throws ComprehendRepositoryException {
        try {
            DetectDominantLanguageRequest request = DetectDominantLanguageRequest.builder()
                    .text(text)
                    .build();

            DetectDominantLanguageResponse response = comprehendClient.detectDominantLanguage(request);

            if (!response.languages().isEmpty()) {
                return response.languages().get(0).languageCode();
            } else {
                return "en"; // Default to English if no languages are detected
            }

        } catch (ComprehendException e) {
            log.error("AWS Comprehend error while detecting language", e);
            throw new ComprehendRepositoryException("AWS Comprehend error", e);
        } catch (Exception e) {
            log.error("Unexpected error accessing Comprehend service", e);
            throw new ComprehendRepositoryException("Error accessing Comprehend service", e);
        }
    }
}
