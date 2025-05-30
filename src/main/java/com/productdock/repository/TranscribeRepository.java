package com.productdock.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdock.exception.TranscribeRepositoryException;
import jakarta.annotation.PostConstruct;
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
    private final ObjectMapper objectMapper;

    @Value("${aws.s3.transcribe.input-bucket}")
    private String bucketName;

    @PostConstruct
    public void init() {
        log.info("TranscribeRepository initialized with bucket '{}'", bucketName);
    }

    /**
     *  Starts a transcription job for the provided audio file in S3.
     *
     * @param s3Key the S3 key of the audio file
     * @return the name of the transcription job
     * @throws TranscribeRepositoryException if an error occurs during the process
     */
    public String startTranscriptionJob(String s3Key) throws TranscribeRepositoryException {
        String jobName = "job-" + UUID.randomUUID();

        Media media = Media.builder()
                .mediaFileUri("s3://" + bucketName + "/" + s3Key)
                .build();

        StartTranscriptionJobRequest request = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(jobName)
                .mediaFormat(MediaFormat.MP3)
                .media(media)
                .identifyLanguage(true)
                .languageOptions(
                        LanguageCode.EN_US,
                        LanguageCode.DE_DE,
                        LanguageCode.FR_FR,
                        LanguageCode.ES_ES,
                        LanguageCode.SV_SE,
                        LanguageCode.PT_PT
                )
                .build();
        try {
            transcribeClient.startTranscriptionJob(request);
            return jobName;
        } catch (TranscribeException e) {
            log.error("Failed to start transcription job", e);
            throw new TranscribeRepositoryException("Failed to start transcription job", e);
        }
    }

    /**
     * Checks the status of a transcription job.
     *
     * @param jobName the name of the transcription job
     * @return the status of the transcription
     * @throws TranscribeRepositoryException if an error occurs during the process
     */
    public String getJobStatus(String jobName) throws TranscribeRepositoryException {
        try {
            GetTranscriptionJobResponse response = transcribeClient.getTranscriptionJob(
                    GetTranscriptionJobRequest.builder()
                            .transcriptionJobName(jobName)
                            .build()
            );
            return response.transcriptionJob().transcriptionJobStatusAsString();
        } catch (TranscribeException e) {
            log.error("Failed to get job status for {}", jobName, e);
            throw new TranscribeRepositoryException("Failed to get transcription job status", e);
        }
    }

    /**
     * Fetches the transcript of a completed transcription job.
     *
     * @param jobName the name of the transcription job
     * @return the transcript text
     * @throws TranscribeRepositoryException if an error occurs during the process
     */
    public String fetchTranscript(String jobName) throws TranscribeRepositoryException {
        try {
            GetTranscriptionJobResponse response = transcribeClient.getTranscriptionJob(
                    GetTranscriptionJobRequest.builder()
                            .transcriptionJobName(jobName)
                            .build()
            );

            String transcriptUrl = response.transcriptionJob().transcript().transcriptFileUri();

            try (InputStream in = new URL(transcriptUrl).openStream()) {
                JsonNode json = objectMapper.readTree(in);
                return json.at("/results/transcripts/0/transcript").asText();
            }
        } catch (TranscribeException | java.io.IOException e) {
            log.error("Failed to fetch transcript for job {}", jobName, e);
            throw new TranscribeRepositoryException("Failed to fetch transcript", e);
        }
    }

    /**
     * Deletes a transcription job.
     *
     * @param jobName the name of the transcription job to delete
     * @throws TranscribeRepositoryException if an error occurs during the deletion
     */
    public void deleteTranscriptionJob(String jobName) throws TranscribeRepositoryException {
        try {
            transcribeClient.deleteTranscriptionJob(DeleteTranscriptionJobRequest.builder()
                    .transcriptionJobName(jobName)
                    .build());
        } catch (TranscribeException e) {
            log.error("Failed to delete transcription job {}", jobName, e);
            throw new TranscribeRepositoryException("Failed to delete transcription job", e);
        }
    }
}
