package com.productdock.repository;

import com.productdock.exception.S3RepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class S3RepositoryTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Repository s3Repository;

    private static final String BUCKET_NAME = "my-test-bucket";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject bucket name via reflection since it's a @Value field
        org.springframework.test.util.ReflectionTestUtils.setField(s3Repository, "bucketName", BUCKET_NAME);
    }

    @Test
    void shouldUploadAudioFileSuccessfully() {
        // Given
        byte[] content = "test-audio".getBytes();
        MockMultipartFile file = new MockMultipartFile("audioFile", "test.mp3", "audio/mpeg", content);

        // When
        assertDoesNotThrow(() -> {
            String s3Key = s3Repository.uploadAudioFile(file);
            assertNotNull(s3Key);
            assertTrue(s3Key.endsWith(".mp3"));
        });

        // Then
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void shouldThrowExceptionWhenS3FailsOnUpload() {
        // Given
        byte[] content = "bad-audio".getBytes();
        MockMultipartFile file = new MockMultipartFile("audioFile", "fail.mp3", "audio/mpeg", content);

        doThrow(S3Exception.builder().message("error").build())
                .when(s3Client)
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));

        // When
        S3RepositoryException exception = assertThrows(S3RepositoryException.class, () -> {
            s3Repository.uploadAudioFile(file);
        });

        // Then
        assertEquals("Error uploading file to S3", exception.getMessage());
    }

    @Test
    void shouldDeleteAudioFileSuccessfully() {
        // Given
        String key = "audio-test.mp3";

        // When
        assertDoesNotThrow(() -> s3Repository.deleteAudioFile(key));

        // Then
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenS3FailsOnDelete() {
        // Given
        String key = "invalid.mp3";

        doThrow(S3Exception.builder().message("error").build())
                .when(s3Client)
                .deleteObject(any(DeleteObjectRequest.class));

        // When
        S3RepositoryException exception = assertThrows(
                S3RepositoryException.class, () -> {
                s3Repository.deleteAudioFile(key);
        });

        // Then
        assertEquals("Error deleting file from S3", exception.getMessage());
    }
}
