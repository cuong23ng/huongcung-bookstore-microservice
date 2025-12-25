package com.huongcung.catalogservice.controller;

import com.huongcung.catalogservice.catalog.enumeration.BookType;
import com.huongcung.catalogservice.common.enumeration.City;
import com.huongcung.catalogservice.common.enumeration.Language;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookDetailsResponse;
import com.huongcung.catalogservice.catalog.model.dto.response.GetBookFrontPageResponse;
import com.huongcung.catalogservice.catalog.service.StoreFrontBookService;
import com.huongcung.catalogservice.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/catalog/books")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class StoreFrontController {

    private final StoreFrontBookService bookService;

    /**
     * Search books with filters, pagination, and faceting
     *
     * @param q Search query string
     * @param genres Genre filters (can be multiple)
     * @param languages Language filters (can be multiple)
     * @param bookTypes Format filters: PHYSICAL, EBOOK, BOTH (can be multiple)
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @param sort Sort option: relevance, price_asc, price_desc, date_desc, rating_desc
     * @return Search results with books, facets, and pagination
     */
    @GetMapping
    public ResponseEntity<BaseResponse> getBooks(
            @PageableDefault(size = 20, page = 1) Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) List<Language> languages,
            @RequestParam(required = false) List<BookType> bookTypes,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sort) {

        // TODO: Split sort into sortField and sortOrder

        GetBookFrontPageResponse response =
                bookService.getOrSearchBooks(pageable, q, genres, languages, bookTypes, minPrice, maxPrice, sort);

        return ResponseEntity.ok(BaseResponse.builder()
                .data(response)
                .build());
    }

    /**
     * Get book details
     * @return book
     */
    @GetMapping("/{code}")
    public ResponseEntity<BaseResponse> getBookDetails(@PathVariable String code) {
        GetBookDetailsResponse book = bookService.getBookDetails(code);
        return ResponseEntity.ok(BaseResponse.builder().data(book).build());
    }

    /**
     * Get autocomplete suggestions for search query
     *
     * @param q Partial query string (required)
     * @param limit Maximum number of suggestions (default: 10)
     * @return List of suggestion strings
     */
    @GetMapping("/suggest")
    public ResponseEntity<BaseResponse> getSuggestions(
            @RequestParam(required = true) String q,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {

        List<String> suggestions = bookService.suggest(q, limit);

        return ResponseEntity.ok(BaseResponse.builder()
                .data(Map.of("suggestions", suggestions))
                .build());
    }
}
