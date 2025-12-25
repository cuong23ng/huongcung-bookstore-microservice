package com.huongcung.catalogservice.catalog.model.dto.request;

import com.huongcung.catalogservice.catalog.enumeration.CoverType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalBookUpdateRequest {

    private String isbn;

    private CoverType coverType;

    private LocalDate publicationDate;

    private Integer weightGrams;

    private Integer heightCm;

    private Integer widthCm;

    private Integer lengthCm;

    private BigDecimal currentPrice;
}
