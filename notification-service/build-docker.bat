@echo off
REM Build Docker image for notification-service
REM Usage: build-docker.bat [tag]
REM Example: build-docker.bat latest
REM Example: build-docker.bat 1.0.0

set TAG=%1
if "%TAG%"=="" set TAG=latest

set IMAGE_NAME=notification-service

echo Building Docker image: %IMAGE_NAME%:%TAG%

docker build -t %IMAGE_NAME%:%TAG% .

if %ERRORLEVEL% EQU 0 (
    echo ✅ Successfully built %IMAGE_NAME%:%TAG%
    echo To run the container:
    echo   docker run -p 8083:8083 %IMAGE_NAME%:%TAG%
) else (
    echo ❌ Failed to build %IMAGE_NAME%:%TAG%
    exit /b 1
)
