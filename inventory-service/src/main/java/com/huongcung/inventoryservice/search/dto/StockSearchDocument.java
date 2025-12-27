package com.huongcung.inventoryservice.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * Solr document model for stock-levels search
 * Maps to Solr core "stock-levels" schema defined in Story 1.1
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockSearchDocument {

    @Field("id")
    private String id;

    @Field("bookTitle")
    private String bookTitle;

    @Field("bookTitleVn")
    private String bookTitleVn;

    @Field("sku")
    private String sku;

    @Field("isbn")
    private String isbn;

    @Field("city")
    private String city;

    @Field("warehouseCode")
    private String warehouseCode;

    @Field("status")
    private String status;

    @Field("createdAt")
    private Date createdAt;

    @Field("lastRestocked")
    private Date lastRestocked;
}

