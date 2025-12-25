package com.huongcung.catalogservice.media.repository;

import com.huongcung.catalogservice.media.model.entity.EbookFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EbookFileRepository extends JpaRepository<EbookFileEntity, Long> {
}
