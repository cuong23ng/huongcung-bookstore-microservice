package com.huongcung.catalogservice.catalog.service;

import com.huongcung.catalogservice.catalog.enumeration.BookType;
import com.huongcung.catalogservice.catalog.model.dto.response.BookFrontPageDTO;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookCatalogPageResponse;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookDetailsResponse;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookFrontPageResponse;
import com.huongcung.catalogservice.common.enumeration.City;
import com.huongcung.catalogservice.common.enumeration.Language;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface StoreFrontBookService {
    GetBookFrontPageResponse getBooksForFrontPage(Pageable pageable);

    GetBookFrontPageResponse getOrSearchBooks(
            Pageable pageable,
            String q,
            List<String> genres,
            List<Language> languages,
            List<BookType> bookTypes,
            Double minPrice,
            Double maxPrice,
            String sort);

    List<String> suggest(String q, Integer limit);

    GetBookDetailsResponse getBookDetails(String code);
    List<BookFrontPageDTO> getBooksForFrontPageByIds(Set<Long> ids);
}
