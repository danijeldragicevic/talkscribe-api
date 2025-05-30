# Talkscribe API
![Java](https://img.shields.io/badge/Java-17-blue)
![AWS SDK](https://img.shields.io/badge/AWS_SDK-2.31.11-blue)
![Docker](https://img.shields.io/badge/Dockerized-yes-blue)<br>
![Build](https://github.com/danijeldragicevic/talkscribe-api/actions/workflows/build.yaml/badge.svg)

## Overview
TalkScribe API is a Spring Boot application that provides an API for text-to-speech and speech-to-text conversion using AWS Polly and Transcribe. You can test and interact with this API through the frontend application available at [https://talkscribe.org/](https://talkscribe.org/).

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
talkscribe-api:<your-image-version>
```
## GitHub Actions CI/CD
This project includes a GitHub Actions workflow to build and push the Docker image to Amazon Elastic Container Registry (ECR). <br>
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
**Notes:** The speech-to-text feature operates asynchronously to handle longer transcription jobs. This process is split into two API calls:
- Start Transcription: Use the `POST /api/speech-to-text` endpoint to submit an audio file for transcription. The response includes a jobName that uniquely identifies the transcription job.
- Check Job Status: Use the `GET /api/speech-to-text/status/{jobName}` endpoint to check the status of the transcription job. When the job status is COMPLETE, the response will include the transcribed text.
- The maximum payload size for the `audioFile` is **1MB**. <br>

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
Content-Type    application/json
---
{
	"jobName": "job-3012093e-40a5-4945-9a81-86ca5ac0f6a3",
	"jobStatus": "IN_PROGRESS",
	"transcript": null
}
```
Example Request:
```commandline
curl --request GET \
  --url http://localhost:8080:8080/api/speech-to-text/status/job-3012093e-40a5-4945-9a81-86ca5ac0f6a3 \
  --header 'User-Agent: insomnia/11.0.0'
```
Example Response:
```commandline
Http-Status     200 OK
Content-Type    application/json
---
{
	"jobName": "job-3012093e-40a5-4945-9a81-86ca5ac0f6a3",
	"jobStatus": "COMPLETED",
	"transcript": "This is my test voice recording."
}
```
### Error Handling
The application provides meaningful error responses, for example:
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
Http-Status     429 Too Many Requests
Content-Type    application/json
---
{
    "timestamp": "2025-04-30T10:17:06.948951",
    "status": 429,
    "error": "Too many requests from IP: 127.0.0.1",
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
    "path": "/api/speech-to-text"
}
```
## Contributing
Contributions are welcome! Feel free to submit a pull request or open an issue.

## License
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
