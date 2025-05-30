package com.productdock.service;

import com.productdock.exception.S3RepositoryException;
import com.productdock.exception.SpeechToTextServiceException;
import com.productdock.exception.TranscribeRepositoryException;
import com.productdock.model.TranscriptionJobResponse;
import com.productdock.repository.S3Repository;
import com.productdock.repository.TranscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeechToTextService {

    private final S3Repository s3Repository;
    private final TranscribeRepository transcribeRepository;
    private final Map<String, String> jobsPendingCleanup = new ConcurrentHashMap<>();

    /**
     * Starts a transcription job and returns the job name and initial status.
     *
     * @param audioFile the audio file to be converted
     * @return TranscriptionJobResponse containing job name and status
     * @throws SpeechToTextServiceException if an error occurs during the process
     */
    //TODO maybe rename this method to: convertSpeechToText
    public TranscriptionJobResponse startTranscriptionJob(MultipartFile audioFile) throws SpeechToTextServiceException {
        try {
            String s3Key = s3Repository.uploadAudioFile(audioFile);
            String jobName = transcribeRepository.startTranscriptionJob(s3Key);

            // Store into the map for scheduled cleanup
            jobsPendingCleanup.put(jobName, s3Key);

            return new TranscriptionJobResponse(jobName, "IN_PROGRESS", null);
        } catch (S3RepositoryException | TranscribeRepositoryException e) {
            log.error("Failed to initiate transcription job", e);
            throw new SpeechToTextServiceException("Failed to start transcription job", e);
        }
    }

    /**
     * Checks the status of the transcription job and fetches transcript if done.
     *
     * @param jobName the transcription job name
     * @return TranscriptionJobResponse containing job status and transcript if available
     * @throws SpeechToTextServiceException if an error occurs during the process
     */
    public TranscriptionJobResponse getTranscriptionJobStatus(String jobName) throws SpeechToTextServiceException {
        try {
            String status = transcribeRepository.getJobStatus(jobName);
            String transcript = null;

            if ("COMPLETED".equals(status)) {
                transcript = transcribeRepository.fetchTranscript(jobName);
            }

            return new TranscriptionJobResponse(jobName, status, transcript);
        } catch (TranscribeRepositoryException | S3RepositoryException e) {
            log.error("Error checking job status or fetching result for job: {}", jobName, e);
            throw new SpeechToTextServiceException("Failed to check job status or fetch transcript", e);
        }
    }

    /**
     * Scheduled cleanup for completed or failed jobs.
     * Runs every 1 hour.
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void scheduledCleanup() {
        log.info("Running scheduled cleanup...");

        for (Map.Entry<String, String> entry : jobsPendingCleanup.entrySet()) {
            String jobName = entry.getKey();
            String s3Key = entry.getValue();

            try {
                String status = transcribeRepository.getJobStatus(jobName);

                if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
                    transcribeRepository.deleteTranscriptionJob(jobName);
                    s3Repository.deleteAudioFile(s3Key);
                    jobsPendingCleanup.remove(jobName);
                    log.info("Cleaned up job '{}' and file '{}'", jobName, s3Key);
                }
            } catch (Exception e) {
                log.warn("Skipping cleanup for job '{}': {}", jobName, e.getMessage());
            }
        }
    }
}
