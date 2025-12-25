package com.huongcung.catalogservice.search.model.dto;

import com.huongcung.catalogservice.catalog.model.dto.BookDTO;
import com.huongcung.catalogservice.common.dto.PaginationInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for book search results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    
    /**
     * List of book search results
     */
    private List<BookDTO> books;
    
    /**
     * Facet counts by field name
     * Key: field name (e.g., "genreNames", "language", "format")
     * Value: list of facets with value and count
     */
    private Map<String, List<SearchFacet>> facets;
    
    /**
     * Pagination metadata
     */
    private PaginationInfo pagination;
    
    /**
     * Highlighted text snippets for search terms
     * Key: book code
     * Value: highlighted text (typically from title or description)
     */
    private Map<String, String> highlightedFields;
    
    /**
     * Query execution time in milliseconds
     */
    private Long executionTimeMs;
    
    /**
     * Whether fallback to database search was used
     */
    @Builder.Default
    private Boolean fallbackUsed = false;
}

