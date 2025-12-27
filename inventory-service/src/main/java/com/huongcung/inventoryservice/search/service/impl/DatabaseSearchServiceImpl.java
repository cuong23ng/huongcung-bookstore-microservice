package com.huongcung.inventoryservice.search.service.impl;

import com.huongcung.inventoryservice.search.dto.SearchFacet;
import com.huongcung.inventoryservice.search.dto.SearchRequest;
import com.huongcung.inventoryservice.search.dto.SearchResponse;
import com.huongcung.inventoryservice.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseSearchServiceImpl implements SearchService {

    @Override
    public SearchResponse searchStocks(SearchRequest request) {
        return null;
    }

    @Override
    public Map<String, List<SearchFacet>> getFacets(SearchRequest request) {
        return Map.of();
    }
}
