package com.huongcung.catalogservice.catalog.mapper;

import com.huongcung.catalogservice.catalog.model.dto.BookDTO;
import com.huongcung.catalogservice.catalog.model.entity.BookEntity;
import com.huongcung.catalogservice.common.mapper.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { AuthorMapper.class, TranslatorMapper.class, PublisherMapper.class, BookImageMapper.class, GenreMapper.class })
public interface BookMapper extends EntityMapper<BookDTO, BookEntity> {

    @Override
    @Mapping(target = "review", ignore = true)
    BookDTO toDto(BookEntity entity);
}
