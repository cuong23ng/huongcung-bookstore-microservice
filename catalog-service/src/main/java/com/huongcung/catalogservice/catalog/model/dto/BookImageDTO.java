package com.huongcung.catalogservice.catalog.model.dto;

import com.huongcung.catalogservice.media.model.dto.ImageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BookImageDTO extends ImageDTO {
    private int position;

    public boolean isCover() {
        return position == 1;
    }

    public boolean isBackCover() {
        return position == 2;
    }
}
