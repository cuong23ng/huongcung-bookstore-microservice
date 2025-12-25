package com.huongcung.catalogservice.media.service.impl;

import com.huongcung.catalogservice.media.model.entity.EbookFileEntity;
import com.huongcung.catalogservice.media.provider.StorageProvider;
import com.huongcung.catalogservice.media.repository.EbookFileRepository;
import com.huongcung.catalogservice.media.service.EbookFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import static com.huongcung.catalogservice.media.constant.FolderConstants.EBOOKS;

@Service
@Slf4j
@RequiredArgsConstructor
public class EbookFileServiceImpl implements EbookFileService {

    private final StorageProvider storageProvider;
    private final EbookFileRepository ebookFileRepository;

    @Override
    public EbookFileEntity saveEbookFromStream(InputStream inputStream, String fileName, String subFolder, String contentType) {
//        String folderPath = EBOOKS + "/" + subFolder;
//        String relativePath = storageProvider.save(inputStream, fileName, folderPath, contentType);
//
//        EbookFileEntity ebook = new EbookFileEntity();
//        ebook.setFileName(fileName);
//        // ebook.setFileType(FileType.findFileTypeByCode(contentType));
//        ebook.setUrl(relativePath);
//        ebook.setDownloadCount(0);
//
//        // ebookFileRepository.save(ebook);
//
//        return ebook;
        return null;
    }
}
