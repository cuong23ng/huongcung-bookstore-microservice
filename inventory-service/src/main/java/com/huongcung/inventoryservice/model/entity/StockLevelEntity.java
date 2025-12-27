package com.huongcung.inventoryservice.model.entity;

import com.huongcung.inventoryservice.common.model.entity.BaseEntity;
import com.huongcung.inventoryservice.enumeration.StockStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_level")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockLevelEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;
    
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 5;
    
    @Column(name = "last_restocked")
    private LocalDateTime lastRestocked;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StockStatus status = StockStatus.OUT_OF_STOCK;
}
