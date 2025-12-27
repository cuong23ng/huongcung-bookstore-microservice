package com.huongcung.inventoryservice.repository;

import com.huongcung.inventoryservice.model.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    boolean existsBySku(String sku);
}
