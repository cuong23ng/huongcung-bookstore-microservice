package com.huongcung.catalogservice.catalog.repository;

import com.huongcung.catalogservice.catalog.model.entity.TranslatorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslatorRepository extends JpaRepository<TranslatorEntity, Long> {
    List<TranslatorEntity> findByIdIn(List<Long> ids);
    Page<TranslatorEntity> findByNameContainingIgnoreCase(String name, org.springframework.data.domain.Pageable pageable);
}


