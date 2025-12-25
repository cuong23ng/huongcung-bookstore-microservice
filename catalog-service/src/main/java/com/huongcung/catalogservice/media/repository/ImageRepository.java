package com.huongcung.catalogservice.media.repository;

import com.huongcung.catalogservice.media.model.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}
