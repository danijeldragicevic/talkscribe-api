package com.productdock.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdock.exception.TranscribeRepositoryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TranscribeRepository {

    private final TranscribeClient transcribeClient;
    private static final int POLL_INTERVAL_MS = 500;

    @Value("${aws.s3.transcribe.input-bucket}")
    private String bucketName;

    /**
     * Transcribes audio from an S3 bucket.
     *
     * @param s3Key the S3 key of the audio file
     * @return the transcribed text
     * @throws TranscribeRepositoryException if an error occurs during transcription
     */
    public String transcribeFromS3(String s3Key) throws TranscribeRepositoryException {
        String jobName = "job-" + UUID.randomUUID();
        try {
            startTranscriptionJob(jobName, s3Key);
            waitForJobCompletion(jobName);

            return fetchTranscript(jobName);

        } catch (Exception e) {
            log.error("Error during transcription job for file: {}", s3Key, e);
            throw new TranscribeRepositoryException("Failed to transcribe audio from S3", e);
        } finally {
            deleteTranscriptionJob(jobName);
        }
    }

    private void startTranscriptionJob(String jobName, String s3Key) {
        String mediaUri = String.format("https://%s.s3.amazonaws.com/%s", bucketName, s3Key);

        StartTranscriptionJobRequest request = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(jobName)
                .media(Media.builder().mediaFileUri(mediaUri).build())
                .identifyLanguage(true)
                .languageOptions(
                        LanguageCode.EN_US,
                        LanguageCode.DE_DE,
                        LanguageCode.FR_FR,
                        LanguageCode.ES_ES,
                        LanguageCode.SV_SE,
                        LanguageCode.PT_PT)
                .build();

        transcribeClient.startTranscriptionJob(request);
    }

    private void waitForJobCompletion(String jobName) throws InterruptedException, TranscribeRepositoryException {
        while (true) {
            GetTranscriptionJobResponse response = transcribeClient.getTranscriptionJob(
                    GetTranscriptionJobRequest.builder().transcriptionJobName(jobName).build()
            );

            TranscriptionJob job = response.transcriptionJob();
            TranscriptionJobStatus status = job.transcriptionJobStatus();

            if (status == TranscriptionJobStatus.COMPLETED) {
                return;
            }
            if (status == TranscriptionJobStatus.FAILED) {
                throw new TranscribeRepositoryException("Transcription job failed: " + job.failureReason());
            }
            Thread.sleep(POLL_INTERVAL_MS);
        }
    }

    private String fetchTranscript(String jobName) throws Exception {
        GetTranscriptionJobResponse response = transcribeClient.getTranscriptionJob(
                GetTranscriptionJobRequest.builder().transcriptionJobName(jobName).build()
        );

        String transcriptUri = response.transcriptionJob().transcript().transcriptFileUri();
        try (InputStream stream = new URL(transcriptUri).openStream()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(stream);

            return json.path("results").path("transcripts").get(0).path("transcript").asText();

        } catch (Exception e) {
            log.error("Failed to fetch or parse transcript for job: {}", jobName, e);
            throw new TranscribeRepositoryException("Failed to fetch transcript from URI", e);
        }
    }

    private void deleteTranscriptionJob(String jobName) {
        transcribeClient.deleteTranscriptionJob(
                DeleteTranscriptionJobRequest.builder().transcriptionJobName(jobName).build()
        );
    }
}
