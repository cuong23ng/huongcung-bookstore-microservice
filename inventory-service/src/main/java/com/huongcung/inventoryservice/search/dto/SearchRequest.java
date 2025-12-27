package com.huongcung.inventoryservice.search.dto;

import com.huongcung.inventoryservice.search.enumeration.SearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    
    /**
     * Query string for full-text search
     */
    private String q;

    @Builder.Default
    private SearchType searchType = SearchType.TITLE;

    private List<String> cities;

    private List<String> warehouseCodes;

    private List<String> status;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 20;
    
    /**
     * Sort option (e.g., "relevance", "price_asc", "price_desc", "date_desc")
     */
    private String sort;
    
    /**
     * Generate cache key string for this request
     * Used by Spring Cache for cache key generation
     */
    @Override
    public String toString() {
        return String.format("q=%s|city=%s|warehouseCodes=%s|status=%s|page=%d|size=%d|sort=%s",
            q != null ? q : "",
            cities != null ? cities : "",
            warehouseCodes != null ? String.join(",", warehouseCodes) : "",
            status != null ? status : "",
            page != null ? page : 1,
            size != null ? size : 20,
            sort != null ? sort : "");
    }
}

