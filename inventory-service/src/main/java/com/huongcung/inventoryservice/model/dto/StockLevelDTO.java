package com.huongcung.inventoryservice.model.dto;

import com.huongcung.inventoryservice.common.enumeration.City;
import com.huongcung.inventoryservice.common.model.dto.BaseDTO;
import com.huongcung.inventoryservice.enumeration.StockStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StockLevelDTO extends BaseDTO {

    // Book
    private String bookTitle;
    private String sku;
    private String isbn;

    // Warehouse
    private Long warehouseId;
    private String warehouseCode;
    private City warehouseCity;
    private String warehouseAddress;

    // Stock
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity; // Calculated: quantity - reservedQuantity
    private Integer reorderLevel;
    private StockStatus status;
    private LocalDateTime lastRestocked;
}

