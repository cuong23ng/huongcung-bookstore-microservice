package com.huongcung.catalogservice.catalog.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EbookInformationDTO {
    private String isbn;
    private LocalDate publicationDate;
    private BigDecimal currentPrice;
}
