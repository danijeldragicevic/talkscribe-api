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
        String key = "audio-" + UUID.randomUUID() + ".wav";
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            s3Client.putObject(putRequest, RequestBody.fromBytes(getBytes(audioFile)));

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

    /**
     * Converts a MultipartFile to a byte array.
     *
     * @param audioFile the MultipartFile to convert
     * @return  the byte array representation of the file
     * @throws S3RepositoryException if an error occurs during conversion
     */
    private byte[] getBytes(MultipartFile audioFile) throws S3RepositoryException {
        try {
            return audioFile.getBytes();

        } catch (Exception e) {
            log.error("Error converting file to bytes", e);
            throw new S3RepositoryException("Error converting file to bytes", e);
        }
    }
}
