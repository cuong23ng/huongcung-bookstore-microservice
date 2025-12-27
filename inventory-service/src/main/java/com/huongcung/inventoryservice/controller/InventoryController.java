package com.huongcung.inventoryservice.controller;

import com.huongcung.inventoryservice.common.enumeration.City;
import com.huongcung.inventoryservice.common.model.dto.BaseResponse;
import com.huongcung.inventoryservice.enumeration.StockStatus;
import com.huongcung.inventoryservice.model.dto.request.StockAdjustmentRequest;
import com.huongcung.inventoryservice.model.dto.request.StockInitRequest;
import com.huongcung.inventoryservice.model.dto.response.StockAdjustmentResponse;
import com.huongcung.inventoryservice.model.dto.response.StockLevelResponse;
import com.huongcung.inventoryservice.search.enumeration.SearchType;
import com.huongcung.inventoryservice.search.service.SearchIndexService;
import com.huongcung.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/inventory")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class InventoryController {

    private final InventoryService inventoryService;
    private final SearchIndexService searchIndexService;

    @GetMapping("/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<BaseResponse> getStockLevels(
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "TITLE") SearchType searchBy,
            @RequestParam(required = false) City city,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) StockStatus status) {

        StockLevelResponse response =
                inventoryService.getStockLevels(pageable, q, searchBy, city, warehouseCode, status);

        return ResponseEntity.ok(BaseResponse.builder()
                .data(response)
                .build());
    }

    @PostMapping("/search/reindex")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<BaseResponse> reindexAllBooks() {
        log.info("Re-indexing all books triggered via API");

        try {
            SearchIndexService.IndexingResult result = searchIndexService.indexAllStockLevels();

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("totalBooks", result.getTotalRecords());
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

    @GetMapping("/stock/{stockLevelId}/adjustments")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<BaseResponse> getStockAdjustments(
            @PathVariable Long stockLevelId,
            @PageableDefault(size = 20, page = 0) Pageable pageable) {

        StockAdjustmentResponse response =
                inventoryService.getStockAdjustments(stockLevelId, pageable);

        return ResponseEntity.ok(BaseResponse.builder()
                .data(response)
                .build());
    }

    @PutMapping("/stock/{stockLevelId}/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<BaseResponse> adjustStock(
            @PathVariable Long stockLevelId,
            @Valid @RequestBody StockAdjustmentRequest request) {

        inventoryService.adjustStock(stockLevelId, request);

        return ResponseEntity.ok(BaseResponse.builder()
                .message("Stock level adjusted successfully")
                .build());
    }

    @PostMapping("/stock/init")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> initStock(@RequestBody StockInitRequest request) {

        inventoryService.initStock(request);

        return ResponseEntity.ok(BaseResponse.builder()
                .message("Stock level initiated successfully")
                .build());
    }
}
