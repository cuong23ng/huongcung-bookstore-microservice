package com.huongcung.catalogservice.catalog.model.dto;

import com.huongcung.catalogservice.catalog.enumeration.BookStatus;
import com.huongcung.catalogservice.common.enumeration.Language;
import com.huongcung.catalogservice.common.dto.BaseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO extends BaseDTO {
    private String code;
    private String title;
    private List<AuthorDTO> authors;
    private List<TranslatorDTO> translators;
    private List<GenreDTO> genres;
    private int edition;
    private PublisherDTO publisher;
    private Language language;
    private int pageCount;
    private String description;
    private List<BookImageDTO> images;

    private EbookInformationDTO ebookInfo;
    private PhysicalBookInformationDTO physicalBookInfo;
    private ReviewDTO review;

    private BookStatus status;

    public boolean hasPhysicalEdition() {
        return physicalBookInfo != null;
    }

    public boolean hasEbookEdition() {
        return ebookInfo != null;
    }
}
