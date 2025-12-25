package com.huongcung.catalogservice.catalog.repository;

import com.huongcung.catalogservice.catalog.model.entity.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {
    List<AuthorEntity> findByIdIn(List<Long> ids);
    Page<AuthorEntity> findByNameContainingIgnoreCase(String name, org.springframework.data.domain.Pageable pageable);
}


