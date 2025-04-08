package com.productdock.repository;

import com.productdock.exception.PollyRepositoryException;
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
public class PollyRepository {
    private final PollyClient pollyClient;

    /**
     * Converts the given text to speech using AWS Polly.
     * This method uses the VoiceId.RUTH to support multiple languages
     * and the neural engine for enhanced voice quality.
     *
     * @param text the text to be converted to speech
     * @param pollyVoiceId the voice ID to be used for synthesis
     * @param pollyLocaleCode the detected language code
     * @return an InputStream containing the synthesized speech
     * @throws PollyRepositoryException if an error occurs during the conversion
     */
    public InputStream convertTextToSpeech(String text, String pollyVoiceId, String pollyLocaleCode) throws PollyRepositoryException {
        try {
            SynthesizeSpeechRequest request = SynthesizeSpeechRequest.builder()
                    .text(text)
                    .voiceId(VoiceId.fromValue(pollyVoiceId))
                    .languageCode(LanguageCode.fromValue(pollyLocaleCode))
                    .outputFormat(OutputFormat.MP3)
                    .engine(Engine.NEURAL)
                    .build();

            return pollyClient.synthesizeSpeech(request);

        } catch (PollyException e) {
            log.error("AWS Polly error while converting text to speech", e);
            throw new PollyRepositoryException("AWS Polly error", e);
        } catch (Exception e) {
            log.error("Unexpected error accessing Polly service", e);
            throw new PollyRepositoryException("Error accessing Polly service", e);
        }
    }
}
