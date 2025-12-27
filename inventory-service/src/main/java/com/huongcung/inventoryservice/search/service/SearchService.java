package com.huongcung.inventoryservice.search.service;

import com.huongcung.inventoryservice.search.dto.SearchFacet;
import com.huongcung.inventoryservice.search.dto.SearchRequest;
import com.huongcung.inventoryservice.search.dto.SearchResponse;

import java.util.List;
import java.util.Map;

public interface SearchService {
    
    /**
     * Search books with filters, pagination, and faceting
     * 
     * @param request Search request with query, filters, pagination
     * @return Search response with results, facets, and pagination
     */
    SearchResponse searchStocks(SearchRequest request);
    
    /**
     * Get facet counts for search request
     * 
     * @param request Search request (filters applied, but results not needed)
     * @return Map of field name to list of facets with counts
     */
    Map<String, List<SearchFacet>> getFacets(SearchRequest request);
}

