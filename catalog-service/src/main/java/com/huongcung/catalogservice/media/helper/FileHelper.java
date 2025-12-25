package com.huongcung.catalogservice.media.helper;

import com.huongcung.catalogservice.media.service.ImageService;
import org.springframework.stereotype.Component;
import org.mapstruct.Named;

@Component
public class FileHelper {

    private final ImageService imageService;

    public FileHelper(ImageService imageService) {
        this.imageService = imageService;
    }

    @Named("buildFullUrl")
    public String buildFullUrl(String relativePath) {
        if (relativePath == null || imageService == null) {
            return relativePath;
        }
        return imageService.getFullUrl(relativePath);
    }
}

