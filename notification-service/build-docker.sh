#!/bin/bash

# Build Docker image for notification-service
# Usage: ./build-docker.sh [tag]
# Example: ./build-docker.sh latest
# Example: ./build-docker.sh 1.0.0

TAG=${1:-latest}
IMAGE_NAME="notification-service"

echo "Building Docker image: ${IMAGE_NAME}:${TAG}"

docker build -t ${IMAGE_NAME}:${TAG} .

if [ $? -eq 0 ]; then
    echo "✅ Successfully built ${IMAGE_NAME}:${TAG}"
    echo "To run the container:"
    echo "  docker run -p 8083:8083 ${IMAGE_NAME}:${TAG}"
else
    echo "❌ Failed to build ${IMAGE_NAME}:${TAG}"
    exit 1
fi
