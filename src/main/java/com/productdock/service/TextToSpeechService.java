package com.productdock.service;

import com.productdock.exception.TextToSpeechRepositoryException;
import com.productdock.exception.TextToSpeechServiceException;
import com.productdock.repository.TextToSpeechRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Service class for handling text-to-speech conversion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TextToSpeechService {
    private final TextToSpeechRepository textToSpeechRepository;

    /**
     * Calls the repository to convert the given text to speech.
     *
     * @param text the text to be converted to speech
     * @return a InputStreamResource containing the synthesized speech
     * @throws TextToSpeechServiceException if an error occurs during the conversion
     */
    public InputStreamResource convertTextToSpeech(String text) throws TextToSpeechServiceException {
        try {
            InputStream audioStream = textToSpeechRepository.convertTextToSpeech(text);
            return new InputStreamResource(audioStream);
        } catch (TextToSpeechRepositoryException e) {
            log.error("Failed to convert text to speech: {}", getTruncatedText(text), e);
            throw new TextToSpeechServiceException("Error processing text-to-speech request", e);
        }
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
