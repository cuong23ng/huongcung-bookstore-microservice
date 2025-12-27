package com.huongcung.inventoryservice.search.dto;

import com.huongcung.inventoryservice.common.model.dto.PaginationInfo;
import com.huongcung.inventoryservice.model.dto.StockLevelDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    private List<StockLevelDTO> stockLevels;

    private Map<String, List<SearchFacet>> facets;

    private PaginationInfo pagination;

    private Map<String, String> highlightedFields;

    private Long executionTimeMs;

    @Builder.Default
    private Boolean fallbackUsed = false;
}

