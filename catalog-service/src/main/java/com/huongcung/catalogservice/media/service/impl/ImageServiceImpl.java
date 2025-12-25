package com.huongcung.catalogservice.media.service.impl;

import com.huongcung.catalogservice.catalog.model.dto.request.ImageBase64Upload;
import com.huongcung.catalogservice.catalog.model.entity.BookEntity;
import com.huongcung.catalogservice.catalog.model.entity.BookImageEntity;
import com.huongcung.catalogservice.media.enumeration.FileType;
import com.huongcung.catalogservice.media.enumeration.MediaStatus;
import com.huongcung.catalogservice.media.model.dto.response.UploadResponse;
import com.huongcung.catalogservice.media.repository.BookImageRepository;
import com.huongcung.catalogservice.media.repository.ImageRepository;
import com.huongcung.catalogservice.media.service.ImageService;
import com.huongcung.catalogservice.media.provider.StorageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.huongcung.catalogservice.media.constant.FolderConstants.BOOKS;
import static com.huongcung.catalogservice.media.constant.FolderConstants.IMAGES;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final BookImageRepository bookImageRepository;
    private final StorageProvider storageProvider;

    @Override
    public List<BookImageEntity> saveBookImagesFromBase64(BookEntity book, List<ImageBase64Upload> uploadImages) {

        List<BookImageEntity> savedImages = new ArrayList<>();
        for (int i = 0; i < uploadImages.size(); i++) {
            ImageBase64Upload imageData = uploadImages.get(i);

            if (imageData == null || !StringUtils.hasText(imageData.getBase64Data())) {
                log.warn("Skipping null image data at index {} for book: {}", i, book.getCode());
                continue;
            }

            try {
                Integer position = imageData.getId();
                if (position == null || position <= 0) {
                    position = i + 1;
                }

                String fileName = imageData.getFileName();
                if (fileName == null || fileName.isBlank()) {
                    // Default filename
                    fileName = "image_" + book.getCode() + "_" + position + ".jpg";
                }

                // Save image to S3 using StorageService
                String folderPath = IMAGES + "/" + BOOKS;
                String contentType = imageData.getFileType() != null ? imageData.getFileType() : "image/jpeg";

                // Build path
                String key = buildKey(fileName, folderPath);

                String relativePath = storageProvider.save(
                        key,
                        imageData.getBase64Data(),
                        contentType
                );

                // Create BookImageEntityv2
                BookImageEntity bookImage = new BookImageEntity();
                bookImage.setBook(book);
                bookImage.setUrl(relativePath);
                bookImage.setAltText(fileName);
                bookImage.setFileName(fileName);
                bookImage.setFileType(FileType.findFileTypeByCode(contentType));
                bookImage.setPosition(position);
                bookImage.setStatus(MediaStatus.COMPLETED);

                // Save the image entity
                BookImageEntity savedImage = bookImageRepository.save(bookImage);
                savedImages.add(savedImage);

                log.info("Image uploaded successfully for book: {}, imageId: {}, position: {}, url: {}",
                        book.getCode(), savedImage.getId(), position, relativePath);

            } catch (Exception e) {
                log.info("Failed to upload image at index {} for book: {}", i, book.getCode(), e);
            }
        }
        return savedImages;
    }

    @Override
    @Deprecated
    public List<BookImageEntity> saveBookImages(BookEntity book, MultipartFile[] files, String subFolder) {
        log.info("Uploading {} images for book ID: {}", files.length, book.getId());
        int lastIndex = book.getImages().size();
        List<BookImageEntity> savedEntities = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file.isEmpty()) {
                continue;
            }

            try {
                int currentIndex = lastIndex + i + 1;
                // Get filename
                String fileName = file.getOriginalFilename();
                if (fileName == null || fileName.isBlank()) {
                    fileName = "image_" + book.getCode() + "_" + currentIndex + ".jpg";
                }

                // Get content type
                String contentType = file.getContentType();
                if (contentType == null || contentType.isBlank()) {
                    contentType = "image/jpeg";
                }

                // Build path
                String key = buildKey(fileName, subFolder);

                // Save image to S3 with correct folder path
                String relativePath = storageProvider.save(
                        key,
                        file.getInputStream(),
                        contentType
                );

                // Create BookImageEntity
                BookImageEntity bookImage = new BookImageEntity();
                bookImage.setBook(book);
                bookImage.setUrl(relativePath);
                bookImage.setAltText(fileName);
                bookImage.setFileName(fileName);
                bookImage.setFileType(FileType.findFileTypeByCode(contentType));
                bookImage.setPosition(currentIndex);

                BookImageEntity savedEntity = bookImageRepository.save(bookImage);
                savedEntities.add(savedEntity);

                log.debug("Image uploaded for book ID: {}, position: {}, url: {}", book.getId(), currentIndex, relativePath);
            } catch (Exception e) {
                log.error("Failed to upload image for book ID: {}", book.getId(), e);
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        return savedEntities;
    }

    @Override
    public UploadResponse prepareUpload(String fileName, Integer id, String folderPath, String contentType) {
        String key = buildKey(fileName, folderPath);
        String uploadUrl = storageProvider.generatePresignedUrl(key, contentType, 10);

        return UploadResponse.builder()
                .key(key)
                .id(id)
                .uploadUrl(uploadUrl)
                .build();
    }

    @Override
    public String getFullUrl(String relativePath) {
        return storageProvider.getFullUrl(relativePath);
    }

    private String buildKey(String fileName, String folderPath) {
        String key = folderPath.endsWith("/")
                ? folderPath + fileName
                : folderPath + "/" + fileName;

        // Remove leading slash if present
        if (key.startsWith("/")) {
            key = key.substring(1);
        }

        return key;
    }
}

