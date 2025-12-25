package com.huongcung.catalogservice.catalog.repository;

import com.huongcung.catalogservice.catalog.model.entity.PublisherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<PublisherEntity, Long> {
    org.springframework.data.domain.Page<PublisherEntity> findByNameContainingIgnoreCase(String name, org.springframework.data.domain.Pageable pageable);
}


