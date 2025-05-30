package com.productdock.controller;

import com.productdock.model.TranscriptionJobResponse;
import com.productdock.security.RateLimited;
import com.productdock.service.SpeechToTextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/speech-to-text")
public class SpeechToTextController {

    private final SpeechToTextService speechToTextService;

    /**
     * Starts a transcription job for the provided audio file.
     *
     * @param audioFile the audio file to be converted
     * @return ResponseEntity with the converted text and HTTP status code
     */
    @RateLimited(requests = 10, durationMinutes = 5)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TranscriptionJobResponse> convertAudioToText(@RequestParam("audioFile") MultipartFile audioFile) {
        log.info("Received request to start transcription job");
        if (audioFile.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new TranscriptionJobResponse(null, "ERROR", "File is empty"));
        }
        TranscriptionJobResponse response = speechToTextService.startTranscriptionJob(audioFile);
        return ResponseEntity.ok(response);
    }

    /**
     * Checks the status of a transcription job and returns the transcript if completed.
     *
     * @param jobName the transcription job name
     * @return job status and optionally transcript
     */
    @RateLimited(requests = 10, durationMinutes = 5)
    @GetMapping(path = "/status/{jobName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TranscriptionJobResponse> getJobStatus(@PathVariable String jobName) {
        log.info("Checking status for job: {}", jobName);

        TranscriptionJobResponse response = speechToTextService.getTranscriptionJobStatus(jobName);

        return ResponseEntity.ok()
                .headers(getNoCacheHeaders())
                .body(response);
    }

    /**
     * Creates HTTP headers to prevent caching of the response.
     *
     * @return HttpHeaders with no-cache directive
     */
    private HttpHeaders getNoCacheHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noStore().mustRevalidate());
        return headers;
    }
}
