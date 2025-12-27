package com.huongcung.inventoryservice.model.dto.response;

import com.huongcung.inventoryservice.common.model.dto.PaginationInfo;
import com.huongcung.inventoryservice.model.dto.StockAdjustmentDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StockAdjustmentResponse {
    List<StockAdjustmentDTO> adjustments;
    PaginationInfo pagination;
}
