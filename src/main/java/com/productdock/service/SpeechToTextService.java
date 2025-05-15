package com.productdock.service;

import com.productdock.exception.S3RepositoryException;
import com.productdock.exception.SpeechToTextServiceException;
import com.productdock.exception.TranscribeRepositoryException;
import com.productdock.model.TranscriptionJobResponse;
import com.productdock.repository.S3Repository;
import com.productdock.repository.TranscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeechToTextService {

    private final S3Repository s3Repository;
    private final TranscribeRepository transcribeRepository;

    //TODO should I introduce Retry mechanism here?

    /**
     * Starts a transcription job and returns the job name and initial status.
     */
    public TranscriptionJobResponse startTranscriptionJob(MultipartFile audioFile) {
        try {
            String s3Key = s3Repository.uploadAudioFile(audioFile);
            String jobName = transcribeRepository.startTranscriptionJob(s3Key);

            return new TranscriptionJobResponse(jobName, "IN_PROGRESS", null);
        } catch (S3RepositoryException | TranscribeRepositoryException e) {
            log.error("Failed to initiate transcription job", e);
            throw new SpeechToTextServiceException("Failed to start transcription job", e);
        }
    }

    /**
     * Checks the status of the transcription job and fetches transcript if done.
     */
    public TranscriptionJobResponse getTranscriptionJobStatus(String jobName) {
        try {
            String status = transcribeRepository.getJobStatus(jobName);
            String transcript = null;

            if ("COMPLETED".equals(status)) {
                transcript = transcribeRepository.fetchTranscript(jobName);

                // Cleanup only after job is done:
                //transcribeRepository.deleteTranscriptionJob(jobName);
                //transcribeRepository.deleteAudioFileForJob(jobName); // we’ll add this helper
            }

            return new TranscriptionJobResponse(jobName, status, transcript);

        } catch (TranscribeRepositoryException | S3RepositoryException e) {
            log.error("Error checking job status or fetching result for job: {}", jobName, e);
            throw new SpeechToTextServiceException("Failed to check job status or fetch transcript", e);
        }
    }
}
