package com.productdock.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdock.exception.TranscribeRepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TranscribeRepositoryTest {

    @Mock
    private TranscribeClient transcribeClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TranscribeRepository transcribeRepository;

    private static final String BUCKET_NAME = "my-test-bucket";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        org.springframework.test.util.ReflectionTestUtils.setField(transcribeRepository, "bucketName", BUCKET_NAME);
    }

    @Test
    void shouldStartTranscriptionJobSuccessfully() throws TranscribeRepositoryException {

        // When
        assertDoesNotThrow(() -> {
            String jobName = transcribeRepository.startTranscriptionJob("audio-123.mp3");
            assertNotNull(jobName);
            assertTrue(jobName.startsWith("job-"));
        });

        // Then
        verify(transcribeClient, times(1)).startTranscriptionJob(any(StartTranscriptionJobRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenTranscriptionJobFails() {
        // Given
        when(transcribeClient.startTranscriptionJob(any(StartTranscriptionJobRequest.class)))
                .thenThrow(TranscribeException.builder().message("fail").build());

        // When
        TranscribeRepositoryException exception = assertThrows(
                TranscribeRepositoryException.class,
                () -> transcribeRepository.startTranscriptionJob("audio-123.mp3")
        );

        // Then
        assertEquals("Failed to start transcription job", exception.getMessage());
    }

    @Test
    void shouldGetJobStatusSuccessfully() {
        // Given
        GetTranscriptionJobResponse mockResponse = GetTranscriptionJobResponse.builder()
                .transcriptionJob(TranscriptionJob.builder()
                        .transcriptionJobStatus(TranscriptionJobStatus.COMPLETED)
                        .build())
                .build();

        // When
        when(transcribeClient.getTranscriptionJob(any(GetTranscriptionJobRequest.class)))
                .thenReturn(mockResponse);

        // Then
        String status = transcribeRepository.getJobStatus("job-123");
        assertEquals("COMPLETED", status);
    }

    @Test
    void shouldDeleteTranscriptionJobSuccessfully() {
        // Given
        DeleteTranscriptionJobResponse response = DeleteTranscriptionJobResponse.builder().build();

        // When
        when(transcribeClient.deleteTranscriptionJob(any(DeleteTranscriptionJobRequest.class)))
                .thenReturn(response);

        // Then
        assertDoesNotThrow(() -> transcribeRepository.deleteTranscriptionJob("job-delete"));
        verify(transcribeClient, times(1)).deleteTranscriptionJob(any(DeleteTranscriptionJobRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenDeleteFails() {
        // Given
        doThrow(TranscribeException.builder().message("fail").build())
                .when(transcribeClient).deleteTranscriptionJob(any(DeleteTranscriptionJobRequest.class));

        // When
        TranscribeRepositoryException exception = assertThrows(TranscribeRepositoryException.class, () -> {
            transcribeRepository.deleteTranscriptionJob("job-123");
        });

        // Then
        assertEquals("Failed to delete transcription job", exception.getMessage());
    }
}
