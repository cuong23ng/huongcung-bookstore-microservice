package com.huongcung.inventoryservice.mapper;

import com.huongcung.inventoryservice.common.mapper.EntityMapper;
import com.huongcung.inventoryservice.model.dto.BookDTO;
import com.huongcung.inventoryservice.model.entity.BookEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, BookEntity> {
}
