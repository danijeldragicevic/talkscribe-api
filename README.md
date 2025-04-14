# Talkscribe API
![Java](https://img.shields.io/badge/Java-17-blue)
![Docker](https://img.shields.io/badge/dockerized-yes-blue)
![Build](https://github.com/danijeldragicevic/talkscribe-api/actions/workflows/build.yaml/badge.svg)

## Overview
TalkScribe API is a Spring Boot application that provides an API for text-to-speech conversion using AWS Polly. <p> 
It is configured for containerized deployment using [Cloud Native Buildpacks](https://buildpacks.io/).

## Features
- Text-to-speech using AWS Polly
- Multi-language support
- Container image built with Buildpacks
- GitHub Actions pipeline with AWS ECR push

## Supported languages
- Arabic
- German
- English (US)
- Spanish (Castilian)
- France
- Hindi
- Portuguese (Brazil)
- Swedish
- Mandarin Chinese

## Prerequisites
Ensure you have the following installed and configured:
- Java 17+
- Docker 25.0.8+
- AWS account with access to Polly and Comprehend services
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
```
Build and run locally:
```commandline
./mvnw clean install
./mvnw spring-boot:run
```
### Docker Image with Buildpacks
Build Docker Image:
```commandline
./mvnw spring-boot:build-image \
  -Dspring-boot.build-image.imageName=talkscribe-api:latest
```
Run Cointainer Locally:
```commandline
docker run -d -p 8080:8080 \ 
    -e AWS_REGION=$AWS_REGION \ 
    -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \ 
    -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \ 
    talkscribe-api-image:latest
```
## GitHub Actions CI/CD
This project includes a GitHub Actions workflow to build and push the Docker image to Amazon Elastic Container Registry (ECR). <p>
Workflow location: `.github/workflows/build.yaml`

Workflow Jobs:
- Build Image — Uses Buildpacks to generate a Docker image from your code
- Push to ECR — Pushes the image to AWS ECR

## API Usage
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
        "languageCode": "de",
        "languageName": "German",
        "locale": "de-DE",
        "voice": "Vicki"
    },
    {
        "languageCode": "en",
        "languageName": "English (US)",
        "locale": "en-US",
        "voice": "Joanna"
    }
]
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
