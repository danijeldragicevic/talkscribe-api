### Build stage ###
# Compiles the application and prepares the JAR file
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Set working directory inside the container
WORKDIR /app

# Copy only required files to avoid unnecessary layers
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Install Maven dependencies first (improves build caching)
RUN ./mvnw dependency:go-offline

# Build the application
RUN mvn clean package -DskipTests

### Runtime stage ###
# Use a minimal, secure base image to run the app
FROM gcr.io/distroless/java17:latest

# Set working directory for the runtime container
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (Spring Boot default port)
EXPOSE 8080

# Start the application
CMD ["app.jar"]
