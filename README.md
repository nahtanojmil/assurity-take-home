# Health-Monitor Service

A lightweight Java Spring Boot service to periodically check multiple service endpoints, detect availability or version issues, and expose the latest health information as JSON via REST API.

---

## Quickstart

### Prerequisites

- JDK 17
- Gradle 8.3
- Docker & Docker Compose
- .env file to be created
  ```
  # Server and authentication
  SERVER_PORT=9085
  SPRING_SECURITY_USER_NAME=admin
  SPRING_SECURITY_USER_PASSWORD=secret123
  
  # Health check configuration
  CHECK_INTERVAL_MS=30000
  CHECK_OUTPUT_DIR=/app/output
  FILE_DATE_FORMATTER=ddMMyyyyHHmmss
  
  # Services list (JSON array)
  CHECK_SERVICES_JSON='[
    {"name":"user-service","url":"http://user-service:8080/health","expectedVersion":"1.0.0"},
    {"name":"order-service","url":"http://order-service:8080/health","expectedVersion":"2.1.0"}
  ]'
  ```
## Setup
To run off as a JAR or on IDE
```
./gradlew clean bootJar
java -jar build/libs/health-monitor-1.0.0.jar
```
To run as a container
```
docker-compose up -d
```
### Design Overview
-  Spring Boot backend with REST endpoints.
-  Scheduled health checks using a configurable interval.
-  Service configuration loaded from environment variable CHECK_SERVICES_JSON.
-  Health output written as JSON files to a configurable directory.
-  REST API exposes latest file, all files or files specified by datetime, date or time.
```
/api/health-files
/api/health-file-latest
/api/health-file-by-date?datetime=081120251430
``` 
-  Basic Auth for endpoint protection.
### Trade offs
## Environment variable (.env) vs YAML/properties
- Pros: Easy to override at runtime, ideal for Docker and Kubernetes.
- Cons: Harder to maintain complex configurations, editing large JSON arrays in env variables can be error-prone.

## Polling interval (fixed interval)
- Pros: Simple to implement, predictable resource usage.
- Cons: Not reactive. Can miss temporary failures or create unnecessary load if interval is too short. Event-driven health checks would be more efficient and effective.

## Single-threaded scheduled checks
- Pros: Predictable, avoids concurrency issues, simple to test.
- Cons: Not ideal for monitoring many services or high-latency endpoints; asynchronous or parallel checks could improve performance.
