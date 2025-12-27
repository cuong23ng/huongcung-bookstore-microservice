package com.huongcung.inventoryservice.model.dto;

import com.huongcung.inventoryservice.common.model.dto.BaseDTO;
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
public class BookDTO extends BaseDTO {
    private String sku;
    private String title;
    private String isbn;
}
