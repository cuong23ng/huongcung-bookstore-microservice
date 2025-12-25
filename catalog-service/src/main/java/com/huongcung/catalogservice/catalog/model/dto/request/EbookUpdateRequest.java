package com.huongcung.catalogservice.catalog.model.dto.request;

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
public class EbookUpdateRequest {
    @NotBlank(message = "ISBN is required")
    private String isbn;

    private LocalDate publicationDate;

    @NotNull(message = "Price is required")
    private BigDecimal currentPrice;
}
