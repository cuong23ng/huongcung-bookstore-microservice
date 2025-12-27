package com.huongcung.catalogservice.client.dto.request;

import com.huongcung.catalogservice.client.dto.StockBookDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInitRequest {
    private StockBookDTO book;
    private Integer quantity;
}
