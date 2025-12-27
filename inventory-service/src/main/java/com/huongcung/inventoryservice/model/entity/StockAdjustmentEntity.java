package com.huongcung.inventoryservice.model.entity;

import com.huongcung.inventoryservice.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_adjustment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockAdjustmentEntity extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_level_id", nullable = false)
    private StockLevelEntity stockLevel;
    
    @Column(name = "previous_quantity", nullable = false)
    private Integer previousQuantity;
    
    @Column(name = "new_quantity", nullable = false)
    private Integer newQuantity;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "adjusted_by", nullable = false)
    private String adjustedBy; // Username
    
    @Column(name = "adjusted_at", nullable = false, updatable = false)
    private LocalDateTime adjustedAt;
}

