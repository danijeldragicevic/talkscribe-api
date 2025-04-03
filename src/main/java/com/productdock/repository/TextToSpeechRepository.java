package com.productdock.repository;

import com.productdock.exception.TextToSpeechRepositoryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.InputStream;

/**
 * Repository for converting text to speech using AWS Polly.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TextToSpeechRepository {
    private final PollyClient pollyClient;

    /**
     * Converts the given text to speech using AWS Polly.
     * This method uses the VoiceId.RUTH to support multiple languages
     * and the neural engine for enhanced voice quality.
     *
     * @param text the text to be converted to speech
     * @return an InputStream containing the synthesized speech
     * @throws TextToSpeechRepositoryException if an error occurs during the conversion
     */
    public InputStream convertTextToSpeech(String text) throws TextToSpeechRepositoryException {
        try {
            SynthesizeSpeechRequest synthesizeSpeechRequest = SynthesizeSpeechRequest.builder()
                    .text(text)
                    .voiceId(VoiceId.RUTH)
                    .engine(Engine.NEURAL)
                    .outputFormat(OutputFormat.MP3)
                    .build();
            return pollyClient.synthesizeSpeech(synthesizeSpeechRequest);
        } catch (PollyException e) {
            log.error("AWS Polly error while converting text to speech", e);
            throw new TextToSpeechRepositoryException("AWS Polly error", e);
        } catch (Exception e) {
            log.error("Unexpected error accessing Polly service", e);
            throw new TextToSpeechRepositoryException("Error accessing Polly service", e);
        }
    }
}
