package com.huongcung.catalogservice.controller;

import com.huongcung.catalogservice.catalog.enumeration.BookType;
import com.huongcung.catalogservice.catalog.model.dto.BookDTO;
import com.huongcung.catalogservice.catalog.model.dto.request.BookCreateRequest;
import com.huongcung.catalogservice.catalog.model.dto.request.BookUpdateRequest;
import com.huongcung.catalogservice.catalog.model.dto.request.EbookUpdateRequest;
import com.huongcung.catalogservice.catalog.model.dto.request.PhysicalBookUpdateRequest;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookCatalogPageResponse;
import com.huongcung.catalogservice.common.enumeration.City;
import com.huongcung.catalogservice.common.enumeration.Language;
import com.huongcung.catalogservice.catalog.service.BackOfficeBookService;
import com.huongcung.catalogservice.common.dto.BaseResponse;
import com.huongcung.catalogservice.media.model.dto.request.BulkUploadRequest;
import com.huongcung.catalogservice.media.model.dto.response.BulkUploadResponse;
import com.huongcung.catalogservice.search.service.SearchIndexService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin/catalog/books")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class BackOfficeBookCatalogController {

    private final BackOfficeBookService bookService;
    private final SearchIndexService searchIndexService;

    @GetMapping
    public ResponseEntity<BaseResponse> getBooks(
            @PageableDefault(size = 20, page = 1) Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) List<Language> languages,
            @RequestParam(required = false) List<BookType> bookTypes,
            @RequestParam(required = false) List<City> cities,
            @RequestParam(required = false) String sort) {
        // TODO: Split sort into sortField and sortOrder

        GetBookCatalogPageResponse response =
                bookService.getOrSearchBooks(pageable, q, genres, languages, bookTypes, cities, sort);

        return ResponseEntity.ok(BaseResponse.builder()
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getBookById(@PathVariable Long id) {
        log.debug("Fetching book by ID: {}", id);

        BookDTO bookDTO = bookService.getBookById(id);

        return ResponseEntity.ok(BaseResponse.builder()
                .data(bookDTO)
                .build());
    }

    /**
     * Trigger re-indexing of all books in Solr
     * This endpoint will re-index all books from the database into Solr search index
     * Useful after schema changes or to fix indexing issues
     *
     * @return BaseResponse containing indexing result with statistics
     */
    @PostMapping("/search/reindex")
    public ResponseEntity<BaseResponse> reindexAllBooks() {
        log.info("Re-indexing all books triggered via API");
        
        try {
            SearchIndexService.IndexingResult result = searchIndexService.indexAllBooks();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("totalBooks", result.getTotalBooks());
            responseData.put("indexedCount", result.getIndexedCount());
            responseData.put("errorCount", result.getErrorCount());
            responseData.put("durationMs", result.getDurationMs());
            responseData.put("successRate", String.format("%.2f%%", result.getSuccessRate()));
            responseData.put("booksPerSecond", result.getDurationMs() > 0 
                ? (result.getIndexedCount() * 1000.0 / result.getDurationMs()) 
                : 0);
            
            log.info("Re-indexing completed: {} indexed, {} errors, {}ms", 
                result.getIndexedCount(), result.getErrorCount(), result.getDurationMs());
            
            return ResponseEntity.ok(BaseResponse.builder()
                    .data(responseData)
                    .message("Re-indexing completed successfully")
                    .build());
        } catch (Exception e) {
            log.error("Failed to re-index books: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.builder()
                            .message("Re-indexing failed: " + e.getMessage())
                            .build());
        }
    }


    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createBook(@Valid @RequestBody BookCreateRequest request) {
        log.info("Creating book: title={}, physical={}, ebook={}"
                , request.getTitle(), request.getHasPhysicalEdition(), request.getHasElectricEdition());

        bookService.createBook(request);

        log.info("Book {} created successfully", request.getTitle());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.builder()
                        .message("Book created successfully")
                        .build());
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<BaseResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request) {
        log.info("Update book: id={}", id);

        bookService.updateBook(id, request);

        log.info("Book {} updated successfully", request.getTitle());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.builder()
                        .message("Book updated successfully")
                        .build());
    }

    @PostMapping("/{id}/create-update/physical")
    public ResponseEntity<BaseResponse> createUpdatePhysicalBook(
            @PathVariable Long id,
            @Valid @RequestBody PhysicalBookUpdateRequest request) {
        log.info("Update book: id={}", id);

        bookService.createOrUpdatePhysicalInfo(id, request);

        log.info("Book {} updated physical info successfully", id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.builder()
                        .message("Book updated successfully")
                        .build());
    }

    @PostMapping("/{id}/create-update/ebook")
    public ResponseEntity<BaseResponse> createUpdateEBook(
            @PathVariable Long id,
            @Valid @RequestBody EbookUpdateRequest request) {
        log.info("Update book: id={}", id);

        bookService.createOrUpdateEbookInfo(id, request);

        log.info("Book {} updated ebook info successfully", id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.builder()
                        .message("Book updated successfully")
                        .build());
    }

    /**
     * Upload book images using MultipartFile
     *
     * @param id the book ID
     * @param files array of image files to upload
     * @return BaseResponse with success message
     */
    @PostMapping("/{id}/images")
    @Deprecated
    public ResponseEntity<BaseResponse> uploadBookImages(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files) {

        bookService.uploadBookImages(id, files);

        return ResponseEntity.ok(BaseResponse.builder()
                .message("Images uploaded successfully")
                .build());
    }

    @PostMapping("/{id}/images/prepare-upload")
    public ResponseEntity<BaseResponse> prepareUploadBookImage(
            @PathVariable Long id,
            @Valid @RequestBody BulkUploadRequest request) {

        BulkUploadResponse response = bookService.prepareImagesUpload(id, request);

        return ResponseEntity.ok(BaseResponse.builder()
                .data(response)
                .build());
    }
}
