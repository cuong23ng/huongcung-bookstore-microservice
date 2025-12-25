package com.huongcung.catalogservice.media.repository;

import com.huongcung.catalogservice.catalog.model.entity.BookImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookImageRepository extends JpaRepository<BookImageEntity, Long> {
    @Query("SELECT bi FROM BookImageEntity bi WHERE bi.book.id = :bookId ORDER BY bi.position ASC")
    List<BookImageEntity> findByBookId(@Param("bookId") Long bookId);
}

