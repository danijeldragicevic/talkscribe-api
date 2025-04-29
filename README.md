# Talkscribe API
![Java](https://img.shields.io/badge/Java-17-blue)
![AWS SDK](https://img.shields.io/badge/AWS_SDK-2.31.11-blue)
![Docker](https://img.shields.io/badge/Dockerized-yes-blue)<br>
![Build](https://github.com/danijeldragicevic/talkscribe-api/actions/workflows/build.yaml/badge.svg)

## Overview
TalkScribe API is a Spring Boot application that provides an API for text-to-speech and speech-to-text conversion using AWS Polly and Transcribe. <p> 
It is configured for containerized deployment using [Cloud Native Buildpacks](https://buildpacks.io/).

## Features
- Text-to-speech using AWS Polly
- Speech-to-text using AWS Transcribe
- Multi-language support

## Supported languages
- English | French | German | Portuguese | Spanish | Swedish

## Prerequisites
Ensure you have the following installed and configured:
- Java 17+
- Docker 25.0.8+
- AWS account with access to Polly, Transcribe, S3 and Comprehend services
- GitHub Secrets for deployment:
  - AWS_ACCESS_KEY_ID 
  - AWS_SECRET_ACCESS_KEY 
  - AWS_REGION 
  - ECR_REPOSITORY_URL

## Installation & Setup
Clone the repository:
```commandline
git clone https://github.com/danijeldragicevic/talkscribe-api
cd talkscribe-api
```
Set up AWS credentials:
```commandline
export AWS_ACCESS_KEY_ID=your-access-key-id
export AWS_SECRET_ACCESS_KEY=your-secret-access-key
export AWS_REGION=your-region
export AWS_S3_TRANSCRIBE_INPUT_BUCKET=your-s3-bucket-name
```
Build and run locally:
```commandline
./mvnw clean install
./mvnw spring-boot:run
```
### Docker Image with Buildpacks
Build Docker Image:
```commandline
./mvnw spring-boot:build-image
```
Run Container Locally:
```commandline
docker run -d --name talkscribe-api \
-p 8080:8080 \
-e AWS_REGION=$AWS_REGION \
-e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
-e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
-e AWS_S3_TRANSCRIBE_INPUT_BUCKET=$AWS_S3_TRANSCRIBE_INPUT_BUCKET \
talkscribe-api:image_version
```
## GitHub Actions CI/CD
This project includes a GitHub Actions workflow to build and push the Docker image to Amazon Elastic Container Registry (ECR). <p>
Workflow location: `.github/workflows/build.yaml`

Workflow Jobs:
- Build Image — Uses Buildpacks to generate a Docker image
- Push to ECR — Pushes the image to AWS ECR

## AWS Permissions
To ensure the application functions correctly, the following permissions must be configured on the user's AWS account.

### Required Role
Create a role (e.g., `talkscribe-task-role`) with the following permissions:

1. **AmazonPolly**: Full access
2. **AmazonTranscribe**: Full access
3. **AmazonComprehend**: Full access
4. **AmazonCloudWatch**: Full access
5. **Amazon S3**: Full access to your  `AWS_S3_TRANSCRIBE_INPUT_BUCKET`

Ensure the role is attached to the resources or services interacting with this application.

## API Usage
### Get supported languages
Endpoint: `GET /api/languages` <br>
Example Request:
```commandline
curl --request GET \
  --url http://localhost:8080/api/languages
```
Example Response:
```commandline
Http-Status     200 OK
Content-Type    application/json
---
[
    {
        "languageCode": "en",
        "languageName": "English",
        "locale": "en-US",
        "voice": "Joanna"
    }
]
```

### Translate text to speech
Endpoint: `POST /text-to-speech` <br>
Example Request:    
```commandline
curl --request POST \
  --url http://localhost:8080/api/text-to-speech \
  --header 'Content-Type: application/json' \
  --data '{
	"text": "Hello, please pronounce this text."
}'
```
Example Response:
```commandline
Http-Status     200 OK
Content-Type    application/octet-stream
---
(Binary audio data)
```

### Translate speech to text
Endpoint: `POST /speech-to-text` <br>
**Note:** The maximum payload size for the `audioFile` is **1MB**.
Example Request:
```commandline
curl --request POST \
  --url http://localhost:8080/api/speech-to-text \
  --header 'Content-Type: multipart/form-data' \
  --header 'User-Agent: insomnia/11.0.0' \
  --form audioFile=@/path/to/your/file/test-message.mp3
```
Example Response:
```commandline
Http-Status     200 OK
Content-Type    application/octet-stream
---
{
    "transcript": "This is my test voice recording."
}
```

### Error Handling
The application provides meaningful error responses, for example:
```commandline
Http-Status     404 Not Found
Content-Type    application/json
---
{
    "timestamp": "2025-04-03T14:16:25.997+00:00",
    "status": 404,
    "error": "Not Found",
    "path": "/api/invalid-endpoint"
}
```
```commandline
Http-Status     413 Payload Too Large
Content-Type    application/json
---
{
    "timestamp": "2025-04-25T15:39:15.668406",
    "status": 413,
    "error": "Maximum upload size exceeded",
    "path": "/api/speech-to-text"
}
```
```commandline
Http-Status     503 Service Unavailable
Content-Type    application/json
---
{
    "timestamp": "2025-04-03T14:18:32.184467",
    "status": 503,
    "error": "Error processing text-to-speech request",
    "path": "/api/text-to-speech"
}
```
## Contributing
Contributions are welcome! Feel free to submit a pull request or open an issue.

## License
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
