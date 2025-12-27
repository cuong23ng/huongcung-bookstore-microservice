package com.huongcung.inventoryservice.search.service.impl;

import com.huongcung.inventoryservice.model.entity.StockLevelEntity;
import com.huongcung.inventoryservice.repository.StockLevelRepository;
import com.huongcung.inventoryservice.search.repository.SearchRepository;
import com.huongcung.inventoryservice.search.dto.StockSearchDocument;
import com.huongcung.inventoryservice.search.service.SearchIndexService;
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
 * Implementation of SearchIndexService for indexing StockLevel into Solr
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchIndexServiceImpl implements SearchIndexService {
    
    private final SearchRepository searchRepository;
    private final StockLevelRepository stockLevelRepository;
    
    @Value("${solr.indexing.batch-size:1000}")
    private int batchSize;
    
    @Override
    @Transactional
    @CacheEvict(value = {"stockSearchResults", "stockSearchFacets", "stockSearchSuggestions"}, allEntries = true)
    public boolean indexStockLevel(StockLevelEntity stockLevel) {
        try {
            StockSearchDocument document = mapEntityToDocument(stockLevel);
            searchRepository.index(document);
            log.info("Successfully indexed stock level: {} (ID: {})", stockLevel.getBook().getSku(), stockLevel.getId());
            return true;
        } catch (Exception e) {
            log.error("Failed to index stock level {} (ID: {}): {}", stockLevel.getBook().getSku(), stockLevel.getId(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public IndexingResult indexAllStockLevels() {
        long startTime = System.currentTimeMillis();
        long totalRecords = 0;
        long indexedCount = 0;
        long errorCount = 0;
        
        try {
            log.info("Starting bulk indexing of all stock Level...");
            
            // Fetch all books (lazy collections will be initialized in transaction)
            List<StockLevelEntity> allStockLevels = stockLevelRepository.findAll();
            totalRecords = allStockLevels.size();
            
            if (totalRecords == 0) {
                log.warn("No books found in database to index");
                return new IndexingResult(0, 0, 0, System.currentTimeMillis() - startTime);
            }
            
            log.info("Found {} books to index. Processing in batches of {}", totalRecords, batchSize);
            
            // Process books in batches
            for (int i = 0; i < allStockLevels.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, allStockLevels.size());
                List<StockLevelEntity> batch = allStockLevels.subList(i, endIndex);
                
                try {
                    // Map entities to documents
                    List<StockSearchDocument> documents = batch.stream()
                        .map(this::mapEntityToDocument)
                        .collect(Collectors.toList());
                    
                    // Index batch
                    searchRepository.indexBatch(documents);
                    indexedCount += documents.size();
                    
                    int progress = (int) ((endIndex * 100.0) / totalRecords);
                    log.info("Indexed batch {}-{} of {} ({}% complete)", 
                        i + 1, endIndex, totalRecords, progress);
                    
                } catch (Exception e) {
                    log.error("Failed to index batch {}-{}: {}", i + 1, endIndex, e.getMessage());
                    errorCount += batch.size();
                    
                    // Try to index individual books in the failed batch
                    for (StockLevelEntity stockLevel : batch) {
                        if (indexStockLevel(stockLevel)) {
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
            
            return new IndexingResult(totalRecords, indexedCount, errorCount, durationMs);
            
        } catch (Exception e) {
            log.error("Fatal error during bulk indexing: {}", e.getMessage(), e);
            long durationMs = System.currentTimeMillis() - startTime;
            return new IndexingResult(totalRecords, indexedCount, totalRecords - indexedCount, durationMs);
        }
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"stockSearchResults", "stockSearchFacets", "stockSearchSuggestions"}, allEntries = true)
    public boolean updateStockLevelIndex(Long id) {
        try {
            StockLevelEntity stockLevel = stockLevelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock level not found: " + id));
            
            return indexStockLevel(stockLevel);
        } catch (Exception e) {
            log.error("Failed to update index for Stock level ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @CacheEvict(value = {"stockSearchResults", "stockSearchFacets", "stockSearchSuggestions"}, allEntries = true)
    public boolean deleteStockLevelFromIndex(Long id) {
        try {
            searchRepository.deleteById(String.valueOf(id));
            log.info("Successfully deleted Stock level from index: {}", id);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete Stock level {} from index: {}", id, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Map StockLevelEntity to StockSearchDocument
     */
    private StockSearchDocument mapEntityToDocument(StockLevelEntity stockLevel) {
        StockSearchDocument document = new StockSearchDocument();
        
        // Basic fields
        document.setId(String.valueOf(stockLevel.getId()));
        document.setBookTitle(stockLevel.getBook().getTitle());
        document.setBookTitleVn(stockLevel.getBook().getTitle());
        document.setSku(stockLevel.getBook().getSku());
        document.setIsbn(stockLevel.getBook().getIsbn());
        document.setCity(stockLevel.getWarehouse().getCity().name());
        document.setWarehouseCode(stockLevel.getWarehouse().getCode());
        document.setStatus(stockLevel.getStatus().name());
        if (stockLevel.getCreatedAt() != null) {
            document.setCreatedAt(Date.from(
                    stockLevel.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
            ));
        }
        if (stockLevel.getLastRestocked() != null) {
            document.setLastRestocked(Date.from(
                    stockLevel.getLastRestocked().atZone(ZoneId.systemDefault()).toInstant()
            ));
        }
        
        return document;
    }
}

