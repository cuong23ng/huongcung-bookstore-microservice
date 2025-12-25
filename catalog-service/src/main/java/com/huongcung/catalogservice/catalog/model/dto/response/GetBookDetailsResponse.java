package com.huongcung.catalogservice.catalog.model.dto.response;

import com.huongcung.catalogservice.catalog.model.dto.*;
import com.huongcung.catalogservice.common.enumeration.Language;
import lombok.Data;

import java.util.List;

@Data
public class GetBookDetailsResponse {
    private Long id;
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
    private Boolean hasPhysicalEdition = false;
    private EbookInformationDTO ebookInfo;
    private Boolean hasEbookEdition = false;
    private PhysicalBookInformationDTO physicalBookInfo;
    private BookReviewDTO review;
}
