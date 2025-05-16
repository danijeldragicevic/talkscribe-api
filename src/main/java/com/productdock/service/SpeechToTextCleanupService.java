package com.productdock.service;

import com.productdock.repository.S3Repository;
import com.productdock.repository.TranscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeechToTextCleanupService {

    private final TranscribeRepository transcribeRepository;
    private final S3Repository s3Repository;
    private final Map<String, String> jobToS3KeyMap;
}
