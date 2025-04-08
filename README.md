# Talkscribe API
## Overview
TalkScribe API is a Spring Boot application that provides an API for text-to-speech conversion using AWS Polly.

## Features
- Text-to-speech conversion using AWS Polly

## Supported languages
- English (US)
- German
- France
- Spanish (Castilian)
- Swedish
- Mandarin Chinese
- Arabic
- Hindi
- Portuguese (Brazil)

## Prerequisites
Ensure you have the following installed and configured:
- Java 17 or higher
- Maven 3.9.0 or higher
- Docker 25.0.8 or higher
- AWS account with access to Polly service

## Installation & Setup
Clone the repository:
```commandline
git clone https://github.com/danijeldragicevic/talkscribe-api
cd talkscribe-api
```
Create environment variables for AWS credentials:
```commandline
export AWS_ACCESS_KEY_ID=your-access-key-id
export AWS_SECRET_ACCESS_KEY=your-secret-access-key
export AWS_REGION=your-region
```
Build the project:
```commandline
mvn clean install
```
Run the application using Maven:
```commandline
mvn spring-boot:run
```
Run the application using Docker:
```commandline
docker-compose up --build
```
## Deployment
The project includes a GitHub Actions workflow to build and push the Docker image to Amazon ECR. <p> 
The workflow is defined in `.github/workflows/build.yaml`.

Workflow jobs:
- Build Jar - The workflow builds the JAR file and saves it as an artifact.
- Build Docker  - The workflow builds the Docker image out of the Jar file.
- Push to ECR - The Docker image is pushed to Amazon Elastic Container Registry (ECR).

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
ID3#TSSELavf58.76.100��d�)���!�j22��w'wxy4��D�	�߉\���ר��nO�D'�,
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
