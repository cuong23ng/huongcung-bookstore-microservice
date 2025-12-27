package com.huongcung.inventoryservice.model.dto.request;

import com.huongcung.inventoryservice.model.dto.BookDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInitRequest {
    private BookDTO book;
    private Integer quantity;
}
