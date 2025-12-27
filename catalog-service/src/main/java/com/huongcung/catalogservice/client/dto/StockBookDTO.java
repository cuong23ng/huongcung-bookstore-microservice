package com.huongcung.catalogservice.client.dto;

import com.huongcung.catalogservice.common.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StockBookDTO extends BaseDTO {
    private String sku;
    private String title;
    private String isbn;
}
