package com.huongcung.catalogservice.media.provider.impl;

import com.huongcung.catalogservice.media.configuration.S3ClientConfig;
import com.huongcung.catalogservice.media.provider.StorageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
@Slf4j
public class S3StorageProvider implements StorageProvider {

    private final S3ClientConfig s3ClientConfig;
    private final S3Presigner presigner;
    private final S3Client s3Client;

    @Override
    public String getFullUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return relativePath;
        }

        // Remove leading slash if present
        String cleanPath = relativePath.startsWith("/")
                ? relativePath.substring(1)
                : relativePath;

        // Build full URL: endpoint/bucket/relativePath
        if (s3ClientConfig != null
                && s3ClientConfig.getEndpoint() != null
                && s3ClientConfig.getBucket() != null) {

            String endpoint = s3ClientConfig.getEndpoint();

            if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
                endpoint = "http://" + endpoint;
            }

            if (!endpoint.endsWith("/")) {
                endpoint += "/";
            }

            return endpoint + s3ClientConfig.getBucket() + "/" + cleanPath;
        }

        // Fallback: return relative path as is if config is not available
        return relativePath;
    }

    @Override
    public String save(String key, InputStream inputStream, String contentType) {
        if (s3ClientConfig == null || s3ClientConfig.getBucket() == null || s3ClientConfig.getBucket().isBlank()) {
            throw new IllegalStateException("S3 configuration is not properly set");
        }

        if (ObjectUtils.isEmpty(inputStream)) {
            throw new IllegalArgumentException("Illegal file stream");
        }

        if (!StringUtils.hasText(contentType)) {
            throw new IllegalArgumentException("Missing content type");
        }

        try {
            // Create PutObjectRequest
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3ClientConfig.getBucket())
                    .key(key)
                    .contentType(contentType)
                    .build();

            // Read all bytes from input stream
            byte[] imageBytes = inputStream.readAllBytes();

            // Upload to S3
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

            log.debug("Image uploaded to S3: bucket={}, key={}", s3ClientConfig.getBucket(), key);

            // Return relative path (without bucket name)
            return key;

        } catch (IOException e) {
            log.error("Failed to read image stream", e);
            throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
        }   catch (Exception e) {
            log.error("Failed to upload image to S3", e);
            throw new RuntimeException("Failed to upload image to S3: " + e.getMessage(), e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.warn("Failed to close input stream", e);
            }
        }
    }

    @Override
    public String save(String key, String base64Data, String contentType) {

        if (base64Data == null || base64Data.isBlank()) {
            throw new IllegalArgumentException("Base64 data cannot be null or empty");
        }

        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            log.info("Invalid Base64 data", e);
            throw new IllegalArgumentException("Invalid Base64 data: " + e.getMessage(), e);
        }

        // Convert bytes to InputStream
        InputStream inputStream = new ByteArrayInputStream(imageBytes);

        return save(key, inputStream, contentType);
    }

    @Override
    public String generatePresignedUrl(String key, String contentType, int expirationInMinutes) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3ClientConfig.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationInMinutes))
                .putObjectRequest(objectRequest)
                .build();

        return presigner.presignPutObject(presignRequest).url().toString();
    }

}
