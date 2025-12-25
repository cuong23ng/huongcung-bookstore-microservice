package com.huongcung.catalogservice.media.provider;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public interface StorageProvider {
    String getFullUrl(String relativePath);

    String save(String key, InputStream inputStream, String contentType);

    String save(String key, String base64Data, String contentType);

    String generatePresignedUrl(String key, String contentType, int expirationInMinutes);
}
