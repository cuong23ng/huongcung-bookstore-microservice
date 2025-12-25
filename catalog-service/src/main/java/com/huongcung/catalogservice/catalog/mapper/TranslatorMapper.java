package com.huongcung.catalogservice.catalog.mapper;

import com.huongcung.catalogservice.catalog.model.dto.TranslatorDTO;
import com.huongcung.catalogservice.catalog.model.entity.TranslatorEntity;
import com.huongcung.catalogservice.common.mapper.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TranslatorMapper extends EntityMapper<TranslatorDTO, TranslatorEntity> {
}
