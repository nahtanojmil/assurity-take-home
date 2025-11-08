# Use Eclipse Temurin JDK 17 for building and running
FROM eclipse-temurin:17-jdk-alpine as build

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src

# Grant execute permission to gradlew
RUN chmod +x ./gradlew

# Build the application (produces jar in build/libs)
RUN ./gradlew clean bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create directory for JSON output
RUN mkdir -p /app/output

# Expose server port
EXPOSE 9085

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]
