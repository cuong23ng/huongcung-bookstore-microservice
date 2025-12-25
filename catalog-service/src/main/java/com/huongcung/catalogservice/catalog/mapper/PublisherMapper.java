package com.huongcung.catalogservice.catalog.mapper;

import com.huongcung.catalogservice.catalog.model.dto.PublisherDTO;
import com.huongcung.catalogservice.catalog.model.entity.PublisherEntity;
import com.huongcung.catalogservice.common.mapper.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PublisherMapper extends EntityMapper<PublisherDTO, PublisherEntity> {
}
