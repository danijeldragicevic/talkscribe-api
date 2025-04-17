package com.productdock.service;

import com.productdock.exception.S3RepositoryException;
import com.productdock.exception.SpeechToTextServiceException;
import com.productdock.exception.TranscribeRepositoryException;
import com.productdock.repository.S3Repository;
import com.productdock.repository.TranscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeechToTextService {
        private final S3Repository s3Repository;
        private final TranscribeRepository transcribeRepository;

        /**
         * Converts a speech audio file to text.
         *
         * @param audioFile the audio file to be converted
         * @return  the transcribed text
         */
        @Retryable(retryFor = SpeechToTextServiceException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
        public String convertAudioToText(MultipartFile audioFile) throws SpeechToTextServiceException {
            String s3Key = null;
            try {
                s3Key = s3Repository.uploadAudioFile(audioFile);
                return transcribeRepository.transcribeFromS3(s3Key);

            } catch (S3RepositoryException e) {
                log.error("Failed to upload audio file to S3: {}", audioFile.getOriginalFilename(), e);
                throw new SpeechToTextServiceException("Error uploading audio file to S3", e);
            } catch (TranscribeRepositoryException e) {
                log.error("Failed to transcribe audio file: {}", audioFile.getOriginalFilename(), e);
                throw new SpeechToTextServiceException("Error transcribing audio file", e);
            } finally {
                if (s3Key != null) {
                    try {
                        s3Repository.deleteAudioFile(s3Key);
                    } catch (S3RepositoryException e) {
                        log.error("Failed to delete audio file from S3: {}", s3Key, e);
                    }
                }
            }
    }

    /**
     * Fallback method triggered when all retry attempts fail.
     */
    @Recover
    public String handleRetriesFailure(SpeechToTextServiceException e, MultipartFile audioFile) throws SpeechToTextServiceException {
        log.error("All retry attempts failed for audio file: {}", audioFile.getOriginalFilename(), e);
        throw e;
    }
}
