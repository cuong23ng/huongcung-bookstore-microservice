package com.huongcung.catalogservice.catalog.service.impl;

import com.huongcung.catalogservice.catalog.converter.BookConverter;
import com.huongcung.catalogservice.catalog.converter.BookDetailsConverter;
import com.huongcung.catalogservice.catalog.enumeration.BookType;
import com.huongcung.catalogservice.catalog.model.dto.BookDTO;
import com.huongcung.catalogservice.catalog.model.dto.response.BookFrontPageDTO;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookDetailsResponse;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookFrontPageResponse;
import com.huongcung.catalogservice.catalog.service.BookService;
import com.huongcung.catalogservice.catalog.service.StoreFrontBookService;
import com.huongcung.catalogservice.common.dto.PaginationInfo;
import com.huongcung.catalogservice.common.enumeration.Language;
import com.huongcung.catalogservice.search.model.dto.SearchRequest;
import com.huongcung.catalogservice.search.model.dto.SearchResponse;
import com.huongcung.catalogservice.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreFrontBookServiceImpl implements StoreFrontBookService {

    private final BookService bookService;
    private final SearchService searchService;
    private final BookConverter bookConverter;
    private final BookDetailsConverter bookDetailsConverter;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "frontPage", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public GetBookFrontPageResponse getBooksForFrontPage(Pageable pageable) {
        Page<BookDTO> books = bookService.findAll(pageable);
        PaginationInfo pagination = PaginationInfo.builder()
                .currentPage(books.getNumber())
                .pageSize(books.getSize())
                .totalResults(books.getTotalElements())
                .totalPages(books.getTotalPages())
                .hasNext(books.hasNext())
                .hasPrevious(books.hasPrevious())
                .build();
        return GetBookFrontPageResponse.builder()
                .books(books.map(bookConverter::convert).toList())
                .pagination(pagination)
                .build();
    }

    @Override
    public GetBookFrontPageResponse getOrSearchBooks(Pageable pageable, String q, List<String> genres, List<Language> languages, List<BookType> bookTypes,
                                                       Double minPrice, Double maxPrice, String sort) {

        log.info("Search request - query: '{}', filters: genre={}, language={}, format={}, page={}, size={}, sort={}, min={}, max={}",
                q, genres, languages, bookTypes, pageable.getPageNumber(), pageable.getPageSize(), sort, minPrice, maxPrice);

        SearchRequest request = SearchRequest.builder()
                .q(q)
                .genres(genres)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .sort(sort)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        if (languages != null) request.setLanguages(languages.stream().map(Language::name).toList());
        if (bookTypes != null) request.setFormats(bookTypes.stream().map(BookType::name).toList());

        SearchResponse searchResponse = searchService.searchBooks(request);

        List<BookFrontPageDTO> bookFrontPageDTOs = searchResponse.getBooks().stream()
                .map(bookConverter::convert)
                .collect(Collectors.toList());

        log.info("Found {} books (page {} of {}) in {}ms",
                searchResponse.getPagination().getTotalResults(),
                searchResponse.getPagination().getCurrentPage(),
                searchResponse.getPagination().getTotalPages(),
                searchResponse.getExecutionTimeMs());

        return GetBookFrontPageResponse.builder()
                .books(bookFrontPageDTOs)
                .pagination(searchResponse.getPagination())
                .facets(searchResponse.getFacets())
                .highlightedFields(searchResponse.getHighlightedFields())
                .build();
    }

    @Override
    public List<String> suggest(String q, Integer limit) {

        log.info("Getting suggestions for query: '{}', limit: {}", q, limit);

        List<String> suggestions = searchService.getSuggestions(q);

        if (limit != null && suggestions.size() > limit) {
            suggestions = suggestions.subList(0, limit);
        }
        log.info("Returning {} suggestions", suggestions.size());
        return suggestions;
    }

    @Override
    @Transactional(readOnly = true)
    public GetBookDetailsResponse getBookDetails(String code) {
        BookDTO bookDTO = bookService.findByCode(code);
        return bookDetailsConverter.convert(bookDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookFrontPageDTO> getBooksForFrontPageByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<BookDTO> books = bookService.findByIds(ids);
        log.info("getBooksForFrontPageByIds found {} results", books.size());

        return books.stream()
                .map(bookConverter::convert)
                .toList();
    }
}
