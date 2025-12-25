package com.huongcung.catalogservice.catalog.mapper;

import com.huongcung.catalogservice.catalog.model.dto.GenreDTO;
import com.huongcung.catalogservice.catalog.model.entity.GenreEntity;
import com.huongcung.catalogservice.common.mapper.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GenreMapper extends EntityMapper<GenreDTO, GenreEntity> {
    
    @Override
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    GenreDTO toDto(GenreEntity entity);
}
