package com.huongcung.catalogservice.catalog.mapper;

import com.huongcung.catalogservice.catalog.model.dto.BookImageDTO;
import com.huongcung.catalogservice.catalog.model.entity.BookImageEntity;
import com.huongcung.catalogservice.common.mapper.EntityMapper;
import com.huongcung.catalogservice.media.helper.FileHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    componentModel = "spring",
    uses = { FileHelper.class }
)
public interface BookImageMapper extends EntityMapper<BookImageDTO, BookImageEntity> {

    @Override
    @Mappings({
            @Mapping(target = "url", source = "url", qualifiedByName = "buildFullUrl"),
            @Mapping(target = "altText", source = "altText")
    })
    BookImageDTO toDto(BookImageEntity entity);
}
