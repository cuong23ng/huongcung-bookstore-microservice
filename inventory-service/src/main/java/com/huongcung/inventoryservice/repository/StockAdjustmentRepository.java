package com.huongcung.inventoryservice.repository;

import com.huongcung.inventoryservice.model.entity.StockAdjustmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustmentEntity, Long> {

    Page<StockAdjustmentEntity> findByStockLevelIdOrderByAdjustedAtDesc(Long stockLevelId, Pageable pageable);

}

