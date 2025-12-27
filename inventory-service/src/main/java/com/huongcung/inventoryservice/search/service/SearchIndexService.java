package com.huongcung.inventoryservice.search.service;

import com.huongcung.inventoryservice.model.entity.StockLevelEntity;

public interface SearchIndexService {

    boolean indexStockLevel(StockLevelEntity stockLevel);

    IndexingResult indexAllStockLevels();

    boolean updateStockLevelIndex(Long id);

    boolean deleteStockLevelFromIndex(Long id);
    
    /**
     * Result of bulk indexing operation
     */
    class IndexingResult {
        private final long totalRecords;
        private final long indexedCount;
        private final long errorCount;
        private final long durationMs;
        
        public IndexingResult(long totalRecords, long indexedCount, long errorCount, long durationMs) {
            this.totalRecords = totalRecords;
            this.indexedCount = indexedCount;
            this.errorCount = errorCount;
            this.durationMs = durationMs;
        }
        
        public long getTotalRecords() {
            return totalRecords;
        }
        
        public long getIndexedCount() {
            return indexedCount;
        }
        
        public long getErrorCount() {
            return errorCount;
        }
        
        public long getDurationMs() {
            return durationMs;
        }
        
        public double getSuccessRate() {
            return totalRecords > 0 ? (double) indexedCount / totalRecords * 100 : 0.0;
        }
    }
}

