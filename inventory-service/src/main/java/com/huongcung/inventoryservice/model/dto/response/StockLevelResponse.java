package com.huongcung.inventoryservice.model.dto.response;

import com.huongcung.inventoryservice.common.model.dto.PaginationInfo;
import com.huongcung.inventoryservice.model.dto.StockLevelDTO;
import com.huongcung.inventoryservice.search.dto.SearchFacet;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class StockLevelResponse {
    private List<StockLevelDTO> stockLevels;
    private PaginationInfo pagination;
    private Map<String, List<SearchFacet>> facets;
    private Map<String, String> highlightedFields;
}
