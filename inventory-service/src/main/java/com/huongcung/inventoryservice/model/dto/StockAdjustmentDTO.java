package com.huongcung.inventoryservice.model.dto;

import com.huongcung.inventoryservice.common.model.dto.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentDTO extends BaseDTO {
    private Long stockLevelId;
    private Integer previousQuantity;
    private Integer newQuantity;
    private Integer difference;
    private String reason;
    private String adjustedBy;
}

