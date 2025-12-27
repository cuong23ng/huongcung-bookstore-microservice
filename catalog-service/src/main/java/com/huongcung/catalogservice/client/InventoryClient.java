package com.huongcung.catalogservice.client;

import com.huongcung.catalogservice.client.dto.request.StockInitRequest;
import com.huongcung.catalogservice.common.configuration.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "http://localhost:8087", configuration = FeignClientConfig.class)
public interface InventoryClient {

    @PostMapping("/api/admin/inventory/stock/init")
    void initStock(@RequestBody StockInitRequest request);
}
