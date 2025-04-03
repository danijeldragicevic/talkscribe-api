package com.productdock.repository;

import com.productdock.exception.TextToSpeechRepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for TextToSpeechRepository.
 */
@ExtendWith(MockitoExtension.class)
class TextToSpeechRepositoryTest {

    @Mock
    private PollyClient pollyClient;

    @InjectMocks
    private TextToSpeechRepository textToSpeechRepository;

    private static final String SAMPLE_TEXT = "Hello, this is a test";

    @BeforeEach
    void setUp() {
        Mockito.reset(pollyClient);
    }

    @Test
    void shouldConvertTextToSpeechSuccessfully() {
        // Given: Mock AWS Polly response
        InputStream mockAudioStream = new ByteArrayInputStream("mock audio data".getBytes());
        ResponseInputStream<SynthesizeSpeechResponse> mockResponse =
                new ResponseInputStream<>(SynthesizeSpeechResponse.builder().build(), mockAudioStream);

        // Mock the PollyClient to return the mock response
        when(pollyClient.synthesizeSpeech(any(SynthesizeSpeechRequest.class))).thenReturn(mockResponse);

        // When: Calling the repository method
        InputStream result = textToSpeechRepository.convertTextToSpeech(SAMPLE_TEXT);

        // Then: Verify correct behavior
        assertNotNull(result, "The response should not be null");
        assertDoesNotThrow(() -> result.read(), "Should return a valid InputStream");

        // Ensure the PollyClient method was called once
        verify(pollyClient, times(1)).synthesizeSpeech(any(SynthesizeSpeechRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenPollyFails() {
        // Given: Simulate AWS Polly failure
        when(pollyClient.synthesizeSpeech(any(SynthesizeSpeechRequest.class)))
                .thenThrow(PollyException.builder().message("AWS Polly Error").build());

        // When: Calling the repository method
        TextToSpeechRepositoryException exception = assertThrows(TextToSpeechRepositoryException.class,
                () -> textToSpeechRepository.convertTextToSpeech(SAMPLE_TEXT));

        // Then: Verify exception handling
        assertEquals("AWS Polly error", exception.getMessage());

        // Ensure the PollyClient method was called once
        verify(pollyClient, times(1)).synthesizeSpeech(any(SynthesizeSpeechRequest.class));
    }
}
