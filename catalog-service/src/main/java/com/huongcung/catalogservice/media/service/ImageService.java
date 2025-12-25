package com.huongcung.catalogservice.media.service;

import com.huongcung.catalogservice.catalog.model.dto.request.ImageBase64Upload;
import com.huongcung.catalogservice.catalog.model.entity.BookEntity;
import com.huongcung.catalogservice.catalog.model.entity.BookImageEntity;
import com.huongcung.catalogservice.media.model.dto.response.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    List<BookImageEntity> saveBookImagesFromBase64(BookEntity book, List<ImageBase64Upload> uploadImages);
    List<BookImageEntity> saveBookImages(BookEntity book, MultipartFile[] files, String subFolder);

    UploadResponse prepareUpload(String fileName, Integer id, String folderPath, String contentType);
    String getFullUrl(String relativePath);
}