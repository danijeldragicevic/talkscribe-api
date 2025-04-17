package com.productdock.controller;

import com.productdock.service.SpeechToTextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/speech-to-text")
public class SpeechToTextController {
    private final SpeechToTextService speechToTextService;

    /**
     * Endpoint to convert audio to text.
     *
     * @param audioFile the audio file to be converted
     * @return ResponseEntity with the converted text and HTTP status code
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> convertAudioToText(@RequestParam("audioFile") MultipartFile audioFile) {
        log.info("Received request to convert audio to text");

        String text = speechToTextService.convertAudioToText(audioFile);
        return ResponseEntity.ok().body(text);
    }
}
