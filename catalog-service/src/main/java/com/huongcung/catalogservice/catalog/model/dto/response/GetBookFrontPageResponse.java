package com.huongcung.catalogservice.catalog.model.dto.response;

import com.huongcung.catalogservice.common.dto.PaginationInfo;
import com.huongcung.catalogservice.search.model.dto.SearchFacet;
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
public class GetBookFrontPageResponse {
    private List<BookFrontPageDTO> books;
    private PaginationInfo pagination;
    private Map<String, List<SearchFacet>> facets;
    private Map<String, String> highlightedFields;
}
