package com.huongcung.catalogservice.catalog.service;

import com.huongcung.catalogservice.catalog.model.dto.BookDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface BookService {
    Page<BookDTO> findAll(Pageable pageable);
    BookDTO findByCode(String code);
    BookDTO findById(Long id);
    List<BookDTO> findByIds(Set<Long> ids);
}
