package com.productdock.repository;

import com.productdock.exception.PollyRepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.PollyException;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for PollyRepository.
 */
@ExtendWith(MockitoExtension.class)
class PollyRepositoryTest {

    @Mock
    private PollyClient pollyClient;

    @InjectMocks
    private PollyRepository pollyRepository;

    private static final String SAMPLE_TEXT = "Hello, this is a test";

    @BeforeEach
    void setUp() {
        Mockito.reset(pollyClient);
    }

    @Test
    void shouldConvertTextToSpeechSuccessfully() {
        // Given
        InputStream audio = new ByteArrayInputStream("audio data".getBytes());
        ResponseInputStream<SynthesizeSpeechResponse> mockResponse =
                new ResponseInputStream<>(SynthesizeSpeechResponse.builder().build(), audio);

        // When
        when(pollyClient.synthesizeSpeech(any(SynthesizeSpeechRequest.class))).thenReturn(mockResponse);
        InputStream result = pollyRepository.convertTextToSpeech(SAMPLE_TEXT, "Joanna", "en-US");

        // Then
        assertNotNull(result, "The response should not be null");
        assertDoesNotThrow(() -> result.read(), "Should return a valid InputStream");

        // Verify
        verify(pollyClient, times(1)).synthesizeSpeech(any(SynthesizeSpeechRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenPollyFails() {
        // When
        when(pollyClient.synthesizeSpeech(any(SynthesizeSpeechRequest.class)))
                .thenThrow(PollyException.builder().message("AWS Polly Error").build());

        PollyRepositoryException exception = assertThrows(PollyRepositoryException.class,
                () -> pollyRepository.convertTextToSpeech(SAMPLE_TEXT, "Joanna", "en-US"));

        // Then
        assertEquals("AWS Polly error", exception.getMessage());

        // Verify
        verify(pollyClient, times(1)).synthesizeSpeech(any(SynthesizeSpeechRequest.class));
    }
}
