# Docker Build Instructions for Notification Service

## Prerequisites

- Docker installed and running
- Maven (optional, if building locally first)

## Building the Docker Image

### Using Docker Build Command

```bash
# Build with default tag (latest)
docker build -t notification-service:latest .

# Build with custom tag
docker build -t notification-service:1.0.0 .

# Build with custom tag and no cache
docker build --no-cache -t notification-service:latest .
```

### Using Build Scripts

**Windows:**
```cmd
build-docker.bat
build-docker.bat 1.0.0
```

**Linux/Mac:**
```bash
chmod +x build-docker.sh
./build-docker.sh
./build-docker.sh 1.0.0
```

## Running the Container

### Basic Run

```bash
docker run -p 8083:8083 notification-service:latest
```

### Run with Environment Variables

```bash
docker run -p 8083:8083 \
  -e SMTP_HOST=smtp.gmail.com \
  -e SMTP_PORT=587 \
  -e SMTP_USERNAME=your-email@gmail.com \
  -e SMTP_PASSWORD=your-app-password \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092 \
  notification-service:latest
```

### Run with Docker Compose

Create a `docker-compose.yml` file:

```yaml
version: '3.8'

services:
  notification-service:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: notification-service
    ports:
      - "8083:8083"
    environment:
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:29092
      SMTP_HOST: ${SMTP_HOST}
      SMTP_PORT: ${SMTP_PORT}
      SMTP_USERNAME: ${SMTP_USERNAME}
      SMTP_PASSWORD: ${SMTP_PASSWORD}
    depends_on:
      - kafka
    networks:
      - platform-network
    restart: unless-stopped

networks:
  platform-network:
    external: true
```

Then run:
```bash
docker-compose up -d
```

## Image Details

- **Base Image**: `eclipse-temurin:17-jre-alpine` (lightweight JRE)
- **Build Image**: `maven:3.9-eclipse-temurin-17` (for building)
- **Port**: 8083
- **User**: Runs as non-root user (`spring`) for security
- **Multi-stage Build**: Optimized for smaller final image size

## Troubleshooting

### Build Fails

1. Check if Maven can download dependencies (network issues)
2. Verify Java version compatibility (requires Java 17)
3. Check for syntax errors in Dockerfile

### Container Exits Immediately

1. Check logs: `docker logs <container-id>`
2. Verify environment variables are set correctly
3. Check if Kafka is accessible from container
4. Verify port 8083 is not already in use

### Health Check Fails

The health check uses a simple HTTP check. If you want more detailed health monitoring, consider adding Spring Boot Actuator to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Then update the health check in Dockerfile to use `/actuator/health` endpoint.

## Pushing to Registry

```bash
# Tag for registry
docker tag notification-service:latest your-registry/notification-service:1.0.0

# Push to registry
docker push your-registry/notification-service:1.0.0
```
