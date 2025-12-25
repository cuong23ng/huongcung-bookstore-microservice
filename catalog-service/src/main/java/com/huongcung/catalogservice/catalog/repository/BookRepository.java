package com.huongcung.catalogservice.catalog.repository;

import com.huongcung.catalogservice.catalog.model.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    Optional<BookEntity> findByCode(String code);
}
