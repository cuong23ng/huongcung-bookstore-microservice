package com.huongcung.inventoryservice.search.service.impl;

import com.huongcung.inventoryservice.common.enumeration.City;
import com.huongcung.inventoryservice.common.model.dto.PaginationInfo;
import com.huongcung.inventoryservice.enumeration.StockStatus;
import com.huongcung.inventoryservice.model.dto.StockLevelDTO;
import com.huongcung.inventoryservice.search.repository.SearchRepository;
import com.huongcung.inventoryservice.search.dto.SearchFacet;
import com.huongcung.inventoryservice.search.dto.SearchRequest;
import com.huongcung.inventoryservice.search.dto.SearchResponse;
import com.huongcung.inventoryservice.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class SolrSearchServiceImpl implements SearchService {
    
    private final SearchRepository searchRepository;
    private final DatabaseSearchServiceImpl databaseSearchService;
    
    @Override
//    @Cacheable(value = "stockSearchResults", key = "#request.toString()", unless = "#result.fallbackUsed == true")
    public SearchResponse searchStocks(SearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Searching stocks with query: '{}', filters: {}", request.getQ(), buildFilterLog(request));
            
            // Build Solr query (pass unescaped query - repository will handle escaping for fuzzy search)
            // Note: We pass the raw query string to allow the repository to build fuzzy queries properly
            String queryString = request.getQ() != null ? request.getQ() : "*:*";
            Map<String, String> filters = buildFilters(request);
            List<String> facetFields = Arrays.asList("city", "status", "warehouseCode");
            
            int start = (request.getPage() - 1) * request.getSize();
            int rows = request.getSize();
            
            // Parse sort parameter
            String sortField = parseSortField(request.getSort());
            String sortOrder = parseSortOrder(request.getSort());
            
            // Execute Solr search
            QueryResponse solrResponse =
                    searchRepository.searchWithFacets(request.getSearchType(), queryString, filters, facetFields, sortField, sortOrder, start, rows);
            
            // Process results
            SearchResponse response = processSolrResponse(solrResponse, request);
            
            long executionTime = System.currentTimeMillis() - startTime;
            response.setExecutionTimeMs(executionTime);
            response.setFallbackUsed(false);
            
            log.info("Search completed in {}ms. Found {} results", executionTime, response.getPagination().getTotalResults());
            
            return response;
            
        } catch (Exception e) {
            log.warn("Solr search failed, falling back to database search: {}", e.getMessage());
            SearchResponse fallbackResponse = databaseSearchService.searchStocks(request);
            return fallbackResponse;
        }
    }
    
    @Override
    @Cacheable(value = "searchFacets", key = "#request.toString()")
    public Map<String, List<SearchFacet>> getFacets(SearchRequest request) {
        
        try {
            log.debug("Getting facets for request: {}", request);
            
            // Pass unescaped query - repository handles escaping for fuzzy search
            String queryString = request.getQ() != null ? request.getQ() : "*:*";
            Map<String, String> filters = buildFilters(request);
            List<String> facetFields = Arrays.asList("city", "status", "warehouseCode");
            
            String sortField = parseSortField(request.getSort());
            String sortOrder = parseSortOrder(request.getSort());
            
            QueryResponse solrResponse = searchRepository.searchWithFacets(
                request.getSearchType(), queryString, filters, facetFields, sortField, sortOrder, 0, 0);
            
            Map<String, List<SearchFacet>> facets = extractFacets(solrResponse);
            
            return facets;
            
        } catch (Exception e) {
            log.warn("Solr facets failed: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
    
    /**
     * Process Solr query response into SearchResponse
     */
    private SearchResponse processSolrResponse(QueryResponse solrResponse, SearchRequest request) {
        SolrDocumentList documents = solrResponse.getResults();

        List<StockLevelDTO> dtoList = documents.stream()
            .map(doc -> {
                log.info("solrResponse FieldValueMap: {}", doc.getFieldValueMap());
                StockLevelDTO stockLevelDTO = new StockLevelDTO();
                Long id = Long.valueOf((String) doc.getFieldValue("id"));
                stockLevelDTO.setId(id);
                String bookTitle = (String) doc.getFieldValue("bookTitle");
                stockLevelDTO.setBookTitle(bookTitle);
                String skuValue = (String) doc.getFieldValue("sku");
                stockLevelDTO.setSku(skuValue);
                String warehouseCode = (String) doc.getFieldValue("warehouseCode");
                stockLevelDTO.setWarehouseCode(warehouseCode);
                String cityValue = (String) doc.getFieldValue("city");
                if (cityValue != null) {
                    City city = City.valueOf(cityValue);
                    stockLevelDTO.setWarehouseCity(city);
                }
                Date createdAt = (Date) doc.getFieldValue("createdAt");
                if (createdAt != null) {
                    stockLevelDTO.setCreatedAt(createdAt.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                }
                Date lastRestocked = (Date) doc.getFieldValue("lastRestocked");
                if (lastRestocked != null) {
                    stockLevelDTO.setLastRestocked(lastRestocked.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                }
                Object statusValue = doc.getFieldValue("status");
                if (statusValue != null) {
                    StockStatus status = StockStatus.valueOf(statusValue.toString());
                    stockLevelDTO.setStatus(status);
                }
                return stockLevelDTO;
            }).toList();
        
        // Extract highlights
        Map<String, String> highlights = extractHighlights(solrResponse, dtoList);
        
        // Extract facets
        Map<String, List<SearchFacet>> facets = extractFacets(solrResponse);

        PaginationInfo pagination = PaginationInfo.builder()
            .currentPage(request.getPage())
            .pageSize(request.getSize())
            .totalResults(documents.getNumFound())
            .totalPages((int) Math.ceil((double) documents.getNumFound() / request.getSize()))
            .hasNext(((long) request.getPage() * request.getSize()) < documents.getNumFound())
            .hasPrevious(request.getPage() > 1)
            .build();
        
        return SearchResponse.builder()
            .stockLevels(dtoList)
            .facets(facets)
            .pagination(pagination)
            .highlightedFields(highlights)
            .build();
    }
    

    private Map<String, String> extractHighlights(QueryResponse solrResponse, List<StockLevelDTO> stockLevelDTOS) {
        Map<String, String> highlights = new HashMap<>();
        
        if (solrResponse.getHighlighting() != null) {
            for (StockLevelDTO stockLevel : stockLevelDTOS) {
                Map<String, List<String>> docHighlights = solrResponse.getHighlighting().get(stockLevel.getId().toString());
                if (docHighlights != null) {
                    docHighlights.getOrDefault("bookTitle", Collections.emptyList())
                            .stream()
                            .findFirst()
                            .ifPresent(highlight -> highlights.put(stockLevel.getId().toString(), highlight));
                }
            }
        }
        
        return highlights;
    }
    
    /**
     * Extract facets from Solr response
     */
    private Map<String, List<SearchFacet>> extractFacets(QueryResponse solrResponse) {
        Map<String, List<SearchFacet>> facets = new HashMap<>();
        
        if (solrResponse.getFacetFields() != null) {
            solrResponse.getFacetFields().forEach(facetField -> {
                List<SearchFacet> facetList = facetField.getValues().stream()
                    .map(count -> SearchFacet.builder()
                        .value(count.getName())
                        .count(count.getCount())
                        .build())
                    .collect(Collectors.toList());
                facets.put(facetField.getName(), facetList);
            });
        }
        
        return facets;
    }
    
    /**
     * Build filter map from SearchRequest
     */
    private Map<String, String> buildFilters(SearchRequest request) {
        Map<String, String> filters = new HashMap<>();
        
        // City filters
        if (request.getCities() != null && !request.getCities().isEmpty()) {
            String cityFilter = request.getCities().stream()
                .map(city -> "\"" + city + "\"")
                .collect(Collectors.joining(" OR "));
            filters.put("city", "(" + cityFilter + ")");
        }
        
        // warehouseCodes filters
        if (request.getWarehouseCodes() != null && !request.getWarehouseCodes().isEmpty()) {
            String warehouseFilter = request.getWarehouseCodes().stream()
                .map(w -> "\"" + w + "\"")
                .collect(Collectors.joining(" OR "));
            filters.put("warehouseCode", "(" + warehouseFilter + ")");
        }

        // Status filters
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            String statusFilter = request.getStatus().stream()
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.joining(" OR "));
            filters.put("status", "(" + statusFilter + ")");
        }
        
        return filters;
    }
    
    /**
     * Escape Solr query string to prevent injection
     */
    private String escapeSolrQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "*:*";
        }
        // Escape special Solr characters: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
        return query.replace("\\", "\\\\")
            .replace("+", "\\+")
            .replace("-", "\\-")
            .replace("&&", "\\&&")
            .replace("||", "\\||")
            .replace("!", "\\!")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace("[", "\\[")
            .replace("]", "\\]")
            .replace("^", "\\^")
            .replace("\"", "\\\"")
            .replace("~", "\\~")
            .replace("*", "\\*")
            .replace("?", "\\?")
            .replace(":", "\\:");
    }
    
    /**
     * Parse sort field from sort parameter
     * Format: "field_order" (e.g., "price_asc", "date_desc", "relevance")
     */
    private String parseSortField(String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return null;
        }
        String[] parts = sort.split("_");
        String field = parts[0].toLowerCase();
        return switch (field) {
            case "price" -> "price";
            case "quantity" -> "quantity";
            default -> {
                log.warn("Unknown sort field: {}, using relevance", field);
                yield null;
            }
        };
    }
    
    /**
     * Parse sort order from sort parameter
     */
    private String parseSortOrder(String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return "desc";
        }
        String[] parts = sort.split("_");
        if (parts.length > 1) {
            String order = parts[1].toLowerCase();
            return "desc".equals(order) ? "desc" : "asc";
        }
        return "desc";
    }
    
    /**
     * Build filter log string for logging
     */
    private String buildFilterLog(SearchRequest request) {
        List<String> filterParts = new ArrayList<>();
        if (request.getCities() != null && !request.getCities().isEmpty()) {
            filterParts.add("cities=" + request.getCities());
        }
        if (request.getWarehouseCodes() != null && !request.getWarehouseCodes().isEmpty()) {
            filterParts.add("warehouseCodes=" + request.getWarehouseCodes());
        }
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            filterParts.add("status=" + request.getStatus());
        }
        return filterParts.isEmpty() ? "none" : String.join(", ", filterParts);
    }
}

