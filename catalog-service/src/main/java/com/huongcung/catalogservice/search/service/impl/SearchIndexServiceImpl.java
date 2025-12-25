package com.huongcung.catalogservice.search.service.impl;

import com.huongcung.catalogservice.catalog.model.entity.*;
import com.huongcung.catalogservice.catalog.repository.BookRepository;
import com.huongcung.catalogservice.search.model.entity.BookSearchDocument;
import com.huongcung.catalogservice.search.repository.BookSearchRepository;
import com.huongcung.catalogservice.search.service.SearchIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SearchIndexService for indexing books into Solr
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchIndexServiceImpl implements SearchIndexService {
    
    private final BookSearchRepository bookSearchRepository;
    private final BookRepository bookRepository;
    
    @Value("${solr.indexing.batch-size:1000}")
    private int batchSize;
    
    @Override
    @Transactional
    @CacheEvict(value = {"searchResults", "searchFacets", "searchSuggestions", "frontPage"}, allEntries = true)
    public boolean indexBook(BookEntity book) {
        try {
            BookSearchDocument document = mapEntityToDocument(book);
            bookSearchRepository.index(document);
            log.info("Successfully indexed book: {} (ID: {})", book.getTitle(), book.getId());
            return true;
        } catch (Exception e) {
            log.error("Failed to index book {} (ID: {}): {}", book.getTitle(), book.getId(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public IndexingResult indexAllBooks() {
        long startTime = System.currentTimeMillis();
        long totalBooks = 0;
        long indexedCount = 0;
        long errorCount = 0;
        
        try {
            log.info("Starting bulk indexing of all books...");
            
            // Fetch all books (lazy collections will be initialized in transaction)
            List<BookEntity> allBooks = bookRepository.findAll();
            totalBooks = allBooks.size();
            
            if (totalBooks == 0) {
                log.warn("No books found in database to index");
                return new IndexingResult(0, 0, 0, System.currentTimeMillis() - startTime);
            }
            
            log.info("Found {} books to index. Processing in batches of {}", totalBooks, batchSize);
            
            // Process books in batches
            for (int i = 0; i < allBooks.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, allBooks.size());
                List<BookEntity> batch = allBooks.subList(i, endIndex);
                
                try {
                    // Map entities to documents
                    List<BookSearchDocument> documents = batch.stream()
                        .map(this::mapEntityToDocument)
                        .collect(Collectors.toList());
                    
                    // Index batch
                    bookSearchRepository.indexBatch(documents);
                    indexedCount += documents.size();
                    
                    int progress = (int) ((endIndex * 100.0) / totalBooks);
                    log.info("Indexed batch {}-{} of {} ({}% complete)", 
                        i + 1, endIndex, totalBooks, progress);
                    
                } catch (Exception e) {
                    log.error("Failed to index batch {}-{}: {}", i + 1, endIndex, e.getMessage());
                    errorCount += batch.size();
                    
                    // Try to index individual books in the failed batch
                    for (BookEntity book : batch) {
                        if (indexBook(book)) {
                            indexedCount++;
                            errorCount--;
                        }
                    }
                }
            }
            
            long durationMs = System.currentTimeMillis() - startTime;
            log.info("Bulk indexing completed: {} indexed, {} errors, {}ms ({} books/sec)", 
                indexedCount, errorCount, durationMs, 
                durationMs > 0 ? (indexedCount * 1000 / durationMs) : 0);
            
            return new IndexingResult(totalBooks, indexedCount, errorCount, durationMs);
            
        } catch (Exception e) {
            log.error("Fatal error during bulk indexing: {}", e.getMessage(), e);
            long durationMs = System.currentTimeMillis() - startTime;
            return new IndexingResult(totalBooks, indexedCount, totalBooks - indexedCount, durationMs);
        }
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"searchResults", "searchFacets", "searchSuggestions", "frontPage"}, allEntries = true)
    public boolean updateBookIndex(Long bookId) {
        try {
            BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
            
            return indexBook(book);
        } catch (Exception e) {
            log.error("Failed to update index for book ID {}: {}", bookId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @CacheEvict(value = {"searchResults", "searchFacets", "searchSuggestions", "frontPage"}, allEntries = true)
    public boolean deleteBookFromIndex(Long bookId) {
        try {
            bookSearchRepository.deleteById(String.valueOf(bookId));
            log.info("Successfully deleted book from index: {}", bookId);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete book {} from index: {}", bookId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Map AbstractBookEntity to BookSearchDocument
     */
    private BookSearchDocument mapEntityToDocument(BookEntity book) {
        BookSearchDocument document = new BookSearchDocument();
        
        // Basic fields
        document.setId(String.valueOf(book.getId()));
        document.setTitle(book.getTitle());
        document.setTitleText(book.getTitle()); // Same as title for Vietnamese text analysis
        document.setDescription(book.getDescription());
        document.setDescriptionText(book.getDescription()); // Same as description for Vietnamese text analysis

        //TODO: Get stock of warehouse

        if (book.getAuthors() != null) {
            List<String> authorNames = book.getAuthors().stream()
                .map(AuthorEntity::getName)
                .filter(name -> name != null && !name.isEmpty())
                .collect(Collectors.toList());
            document.setAuthorNames(authorNames);
        }

        if (book.getGenres() != null) {
            List<String> genreNames = book.getGenres().stream()
                .map(GenreEntity::getCode)
                .filter(code -> code != null && !code.isEmpty())
                .collect(Collectors.toList());
            document.setGenreNames(genreNames);
        }

        if (book.getPublisher() != null) {
            document.setPublisherName(book.getPublisher().getName());
        }

        if (book.getLanguage() != null) {
            document.setLanguage(book.getLanguage().name());
        }
        
        // Prices - get from related PhysicalBookEntity and EbookEntity
        if (book.getPhysicalBookInfo() != null) {
            PhysicalBookEntity physicalBook = book.getPhysicalBookInfo();
            if (physicalBook.getCurrentPrice() != null) {
                document.setPhysicalPrice(physicalBook.getCurrentPrice().doubleValue());
            }
        }

        // Get ebook price
        if (book.getEbookInfo() != null) {
            EbookEntity ebook = book.getEbookInfo();
            if (ebook.getCurrentPrice() != null) {
                document.setEbookPrice(ebook.getCurrentPrice().doubleValue());
            }
        }
        
        // Created timestamp
        if (book.getCreatedAt() != null) {
            document.setCreatedAt(Date.from(
                book.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
            ));
        }

        document.setAverageRating(null);
        document.setReviewCount(null);
        
        return document;
    }
}

