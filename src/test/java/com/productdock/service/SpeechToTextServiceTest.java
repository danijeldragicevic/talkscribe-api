package com.productdock.service;

import com.productdock.exception.S3RepositoryException;
import com.productdock.exception.SpeechToTextServiceException;
import com.productdock.exception.TranscribeRepositoryException;
import com.productdock.model.TranscriptionJobResponse;
import com.productdock.repository.S3Repository;
import com.productdock.repository.TranscribeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeechToTextServiceTest {

    @Mock
    private S3Repository s3Repository;

    @Mock
    private TranscribeRepository transcribeRepository;

    @Mock
    private MultipartFile audioFile;

    @InjectMocks
    private SpeechToTextService speechToTextService;

    @BeforeEach
    void setUp() {
        speechToTextService = new SpeechToTextService(s3Repository, transcribeRepository);
    }

    @Test
    void shouldStartTranscriptionJobSuccessfully() {
        // Given
        when(s3Repository.uploadAudioFile(audioFile)).thenReturn("audio-key.mp3");
        when(transcribeRepository.startTranscriptionJob("audio-key.mp3")).thenReturn("job-123");

        // When
        TranscriptionJobResponse response = speechToTextService.startTranscriptionJob(audioFile);

        // Then
        assertEquals("job-123", response.getJobName());
        assertEquals("IN_PROGRESS", response.getJobStatus());
        assertNull(response.getTranscript());
    }

    @Test
    void shouldThrowExceptionWhenStartFails() {
        // Given
        when(s3Repository.uploadAudioFile(audioFile)).thenThrow(new S3RepositoryException("Failed", new RuntimeException()));

        // When
        assertThrows(SpeechToTextServiceException.class,
                () -> speechToTextService.startTranscriptionJob(audioFile));

        // Then
        verify(transcribeRepository, never()).startTranscriptionJob(any());
    }

    @Test
    void shouldReturnJobStatusWithoutTranscriptSuccessfully() {
        // Given
        when(transcribeRepository.getJobStatus("job-123")).thenReturn("IN_PROGRESS");

        // When
        TranscriptionJobResponse response = speechToTextService.getTranscriptionJobStatus("job-123");

        // Then
        assertEquals("IN_PROGRESS", response.getJobStatus());
        assertNull(response.getTranscript());
    }

    @Test
    void shouldReturnJobStatusWithTranscriptSuccessfully() {
        // Given
        when(transcribeRepository.getJobStatus("job-123")).thenReturn("COMPLETED");
        when(transcribeRepository.fetchTranscript("job-123")).thenReturn("This is my test voice recording.");

        // When
        TranscriptionJobResponse response = speechToTextService.getTranscriptionJobStatus("job-123");

        // Then
        assertEquals("COMPLETED", response.getJobStatus());
        assertEquals("This is my test voice recording.", response.getTranscript());
    }

    @Test
    void shouldThrowExceptionWhenGettingStatusFails() {
        // Given
        when(transcribeRepository.getJobStatus("job-123")).thenThrow(new TranscribeRepositoryException("fail", new RuntimeException()));

        // When/Then
        assertThrows(SpeechToTextServiceException.class,
                () -> speechToTextService.getTranscriptionJobStatus("job-123"));
    }
}
