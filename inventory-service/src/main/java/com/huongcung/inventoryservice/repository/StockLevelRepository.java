package com.huongcung.inventoryservice.repository;

import com.huongcung.inventoryservice.model.entity.BookEntity;
import com.huongcung.inventoryservice.model.entity.StockLevelEntity;
import com.huongcung.inventoryservice.model.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevelEntity, Long> {
    boolean existsByBookAndWarehouse(BookEntity book, WarehouseEntity warehouse);
}

