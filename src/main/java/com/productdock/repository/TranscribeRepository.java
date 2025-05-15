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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TranscribeRepository {

    private final TranscribeClient transcribeClient;
    private final S3Repository s3Repository;
    private final ObjectMapper objectMapper;

    // Maps job name -> S3 key (needed for cleanup later)
    private final Map<String, String> jobToS3KeyMap = new ConcurrentHashMap<>();

    @Value("${aws.s3.transcribe.input-bucket}")
    private String bucketName;

    @PostConstruct
    public void init() {
        log.info("TranscribeRepository initialized with bucket '{}'", bucketName);
    }

    //TODO add comments to all methods

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
            jobToS3KeyMap.put(jobName, s3Key);
            return jobName;

        } catch (TranscribeException e) {
            log.error("Failed to start transcription job", e);
            throw new TranscribeRepositoryException("Failed to start transcription job", e);
        }
    }

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

        } catch (Exception e) {
            log.error("Failed to fetch transcript for job {}", jobName, e);
            throw new TranscribeRepositoryException("Failed to fetch transcript", e);
        }
    }

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

    //TODO maybe move this to S3Repository and than call from the Service layer
    public void deleteAudioFileForJob(String jobName) throws TranscribeRepositoryException {
        String s3Key = jobToS3KeyMap.get(jobName);
        if (s3Key != null) {
            s3Repository.deleteAudioFile(s3Key);
            jobToS3KeyMap.remove(jobName);
        } else {
            log.warn("No S3 key found for job '{}'. File cleanup skipped.", jobName);
        }
    }
}
