package com.huongcung.catalogservice.catalog.service;

import com.huongcung.catalogservice.catalog.enumeration.BookType;
import com.huongcung.catalogservice.common.enumeration.City;
import com.huongcung.catalogservice.common.enumeration.Language;
import com.huongcung.catalogservice.catalog.model.dto.BookDTO;
import com.huongcung.catalogservice.catalog.model.dto.request.BookCreateRequest;
import com.huongcung.catalogservice.catalog.model.dto.request.BookUpdateRequest;
import com.huongcung.catalogservice.catalog.model.dto.request.EbookUpdateRequest;
import com.huongcung.catalogservice.catalog.model.dto.request.PhysicalBookUpdateRequest;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookCatalogPageResponse;
import com.huongcung.catalogservice.media.model.dto.request.BulkUploadRequest;
import com.huongcung.catalogservice.media.model.dto.response.BulkUploadResponse;
import com.huongcung.catalogservice.media.model.dto.response.UploadResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BackOfficeBookService {

    GetBookCatalogPageResponse getOrSearchBooks(
            Pageable pageable,
            String q,
            List<String> genres,
            List<Language> languages,
            List<BookType> bookTypes,
            List<City> cities,
            String sort);

    BookDTO getBookById(Long bookId);

    void createBook(BookCreateRequest request);

    void updateBook(Long bookId, BookUpdateRequest request);

    void createOrUpdatePhysicalInfo(Long bookId, PhysicalBookUpdateRequest request);

    void createOrUpdateEbookInfo(Long bookId, EbookUpdateRequest request);

    void uploadBookImages(Long bookId, MultipartFile[] files);

    UploadResponse prepareImageUpload(Long bookId, Integer position, String fileName, String contentType);

    BulkUploadResponse prepareImagesUpload(Long bookId, BulkUploadRequest uploadRequest);
}
