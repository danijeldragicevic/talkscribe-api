package com.productdock.service;

import com.productdock.exception.ComprehendRepositoryException;
import com.productdock.exception.PollyRepositoryException;
import com.productdock.exception.TextToSpeechServiceException;
import com.productdock.model.VoiceSelection;
import com.productdock.repository.ComprehendRepository;
import com.productdock.repository.PollyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextToSpeechService {
    private final ComprehendRepository comprehendRepository;
    private final PollyRepository pollyRepository;
    private final SupportedVoicesService supportedVoicesService;

    /**
     * Converts text to speech by detecting language, selecting a suitable voice,
     * and synthesizing speech using AWS services.
     * Retries automatically on specific repository failures.
     *
     * @param text the text to be converted to speech
     * @return a InputStreamResource containing the synthesized speech
     * @throws TextToSpeechServiceException if the operation fails after all retry attempts
     */
    @Retryable(retryFor = TextToSpeechServiceException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public InputStreamResource convertTextToSpeech(String text) throws TextToSpeechServiceException {
        try {
            String languageCode = comprehendRepository.detectLanguage(text);
            VoiceSelection voiceSelection = supportedVoicesService.selectVoice(languageCode);

            InputStream audioStream = pollyRepository.convertTextToSpeech(text, voiceSelection.getPollyVoiceId(), voiceSelection.getPollyLocaleCode());
            return new InputStreamResource(audioStream);

        } catch (ComprehendRepositoryException e) {
            log.error("Failed to detect language: {}", getTruncatedText(text), e);
            throw new TextToSpeechServiceException("Error processing text-to-speech request", e);
        } catch (PollyRepositoryException e) {
            log.error("Failed to convert text to speech: {}", getTruncatedText(text), e);
            throw new TextToSpeechServiceException("Error processing text-to-speech request", e);
        }
    }

    /**
     * Fallback method triggered when all retry attempts fail.
     */
    @Recover
    public InputStreamResource handleRetriesFailure(TextToSpeechServiceException e, String text) {
        log.error("All retry attempts failed for text: {}", getTruncatedText(text), e);
        throw e;
    }

    /**
     * Truncates text for logging to avoid excessive output.
     *
     * @param text the full text
     * @return truncated text for logging
     */
    private String getTruncatedText(String text) {
        int maxLength = 50;
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
