package com.productdock.repository;

import com.productdock.exception.S3RepositoryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class S3Repository {

    private final S3Client s3Client;

    @Value("${aws.s3.transcribe.input-bucket}")
    private String bucketName;

    /**
     * Uploads an audio file to S3.
     *
     * @param audioFile the audio file to upload
     * @return the S3 key of the uploaded file
     * @throws S3RepositoryException if an error occurs during the upload
     */
    public String uploadAudioFile(MultipartFile audioFile) throws S3RepositoryException {
        String key = "audio-" + UUID.randomUUID() + ".mp3";

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(audioFile.getContentType())
                .build();

        try (InputStream inputStream = audioFile.getInputStream()) {
            s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, audioFile.getSize()));
        } catch (S3Exception e) {
            log.error("Error uploading file to S3", e);
            throw new S3RepositoryException("Error uploading file to S3", e);
        } catch (Exception e) {
            log.error("Unexpected error uploading file to S3", e);
            throw new S3RepositoryException("Unexpected error uploading file to S3", e);
        }
        return key;
    }


    /**
     * Deletes an audio file from S3.
     *
     * @param s3Key the S3 key of the file to delete
     * @throws S3RepositoryException if an error occurs during the deletion
     */
    public void deleteAudioFile(String s3Key) throws S3RepositoryException {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build());
        } catch (S3Exception e) {
            log.error("Error deleting file from S3", e);
            throw new S3RepositoryException("Error deleting file from S3", e);
        } catch (Exception e) {
            log.error("Unexpected error deleting file from S3", e);
            throw new S3RepositoryException("Unexpected error deleting file from S3", e);
        }
    }
}
