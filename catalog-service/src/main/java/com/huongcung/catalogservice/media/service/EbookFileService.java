package com.huongcung.catalogservice.media.service;

import com.huongcung.catalogservice.media.model.entity.EbookFileEntity;

import java.io.InputStream;

public interface EbookFileService {
    EbookFileEntity saveEbookFromStream(InputStream inputStream, String fileName, String folderPath, String contentType);
}
