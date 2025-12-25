package com.huongcung.catalogservice.catalog.mapper;

import com.huongcung.catalogservice.catalog.model.dto.AuthorDTO;
import com.huongcung.catalogservice.catalog.model.entity.AuthorEntity;
import com.huongcung.catalogservice.common.mapper.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper extends EntityMapper<AuthorDTO, AuthorEntity> {
}
