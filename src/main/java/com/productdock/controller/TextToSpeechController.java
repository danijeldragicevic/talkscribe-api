package com.productdock.controller;

import com.productdock.model.TextToSpeechRequest;
import com.productdock.service.TextToSpeechService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/text-to-speech")
public class TextToSpeechController {
    private final TextToSpeechService textToSpeechService;

    /**
     * Endpoint to convert text to speech.
     *
     * @param request the text-to-speech request containing the text to be converted
     * @return ResponseEntity with the audio stream and HTTP status code
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> convertTextToSpeech(@Valid @RequestBody TextToSpeechRequest request) {
        log.info("Received request to convert text to speech");

        InputStreamResource resource = textToSpeechService.convertTextToSpeech(request.getText());

        return ResponseEntity.ok()
                .headers(getAudioHeaders())
                .body(resource);
    }

    /**
     * Creates HTTP headers for the audio response.
     *
     * @return HttpHeaders with content type set to octet-stream
     */
    private HttpHeaders getAudioHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return headers;
    }
}
