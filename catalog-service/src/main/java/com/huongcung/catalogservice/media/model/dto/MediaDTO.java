package com.huongcung.catalogservice.media.model.dto;

import com.huongcung.catalogservice.common.dto.BaseDTO;
import com.huongcung.catalogservice.media.enumeration.FileType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class MediaDTO extends BaseDTO {
    private String fileName;
    private FileType fileType;
    private String url;
}
