package com.huongcung.catalogservice.catalog.model.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhysicalBookCreatedEvent {
    private String sku;
    private String title;
    private String isbn;
}
