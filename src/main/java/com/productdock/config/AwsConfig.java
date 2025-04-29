package com.productdock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.transcribe.TranscribeClient;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    /**
     * Creates a PollyClient bean.
     * <p>
     * The client will be configured with the specified region and will automatically fetch
     * login credentials from the associated IAM role.
     *
     * @return PollyClient configured with the specified region and default credentials.
    */
    @Bean
    public PollyClient pollyClient() {
        return PollyClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Creates a ComprehendClient bean.
     * <p>
     * The client will be configured with the specified region and will automatically fetch
     * login credentials from the associated IAM role.
     *
     * @return ComprehendClient configured with the specified region and default credentials.
     */
    @Bean
    public ComprehendClient comprehendClient() {
        return ComprehendClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Creates a TranscribeClient bean.
     * <p>
     * The client will be configured with the specified region and will automatically fetch
     * login credentials from the associated IAM role.
     *
     * @return TranscribeClient configured with the specified region and default credentials.
     */
    @Bean
    public TranscribeClient transcribeClient() {
        return TranscribeClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Creates a S3Client bean.
     * <p>
     * The client will be configured with the specified region and will automatically fetch
     * login credentials from the associated IAM role.
     *
     * @return S3Client configured with the specified region and default credentials.
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
