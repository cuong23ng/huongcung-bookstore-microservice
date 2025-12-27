package com.huongcung.inventoryservice.mapper;

import com.huongcung.inventoryservice.common.mapper.EntityMapper;
import com.huongcung.inventoryservice.model.dto.StockAdjustmentDTO;
import com.huongcung.inventoryservice.model.entity.StockAdjustmentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockAdjustmentMapper extends EntityMapper<StockAdjustmentDTO, StockAdjustmentEntity> {
}

