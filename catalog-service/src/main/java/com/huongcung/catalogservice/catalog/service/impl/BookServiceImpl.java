package com.huongcung.catalogservice.catalog.service.impl;

import com.huongcung.catalogservice.catalog.mapper.BookMapper;
import com.huongcung.catalogservice.catalog.model.dto.*;
import com.huongcung.catalogservice.catalog.repository.BookRepository;
import com.huongcung.catalogservice.catalog.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public Page<BookDTO> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::toDto);
    }

    @Override
    public BookDTO findByCode(String code) {
        return bookRepository.findByCode(code).map(bookMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book with code " + code));
    }

    @Override
    public BookDTO findById(Long id) {
        return bookRepository.findById(id).map(bookMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book with id " + id));
    }

    @Override
    public List<BookDTO> findByIds(Set<Long> ids) {
        return bookMapper.toDto(bookRepository.findAllById(ids));
    }
}
