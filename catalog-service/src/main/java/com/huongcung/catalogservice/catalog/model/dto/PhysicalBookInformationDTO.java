package com.huongcung.catalogservice.catalog.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PhysicalBookInformationDTO {
    private String isbn;
    private LocalDate publicationDate;
    private BigDecimal currentPrice;
    private String coverType;
    private Integer weightGrams;
    private Integer heightCm;
    private Integer widthCm;
    private Integer lengthCm;
}
