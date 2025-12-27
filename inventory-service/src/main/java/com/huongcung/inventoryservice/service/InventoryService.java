package com.huongcung.inventoryservice.service;

import com.huongcung.inventoryservice.common.enumeration.City;
import com.huongcung.inventoryservice.enumeration.StockStatus;
import com.huongcung.inventoryservice.model.dto.request.StockAdjustmentRequest;
import com.huongcung.inventoryservice.model.dto.request.StockInitRequest;
import com.huongcung.inventoryservice.model.dto.response.StockAdjustmentResponse;
import com.huongcung.inventoryservice.model.dto.response.StockLevelResponse;
import com.huongcung.inventoryservice.search.enumeration.SearchType;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    StockLevelResponse getStockLevels(Pageable pageable, String q, SearchType searchBy, City city,
                                      String warehouseCode, StockStatus status);
    StockAdjustmentResponse getStockAdjustments(Long stockLevelId, Pageable pageable);
    void adjustStock(Long stockLevelId, StockAdjustmentRequest request);
    void initStock(StockInitRequest request);
}






