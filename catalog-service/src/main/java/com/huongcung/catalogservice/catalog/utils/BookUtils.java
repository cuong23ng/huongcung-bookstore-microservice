package com.huongcung.catalogservice.catalog.utils;

import com.huongcung.catalogservice.catalog.model.entity.BookEntity;
import com.huongcung.catalogservice.catalog.model.entity.EbookEntity;
import com.huongcung.catalogservice.catalog.model.entity.PhysicalBookEntity;
import com.huongcung.catalogservice.media.model.entity.EbookFileEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookUtils {
    
    private static final String PHYSICAL_PREFIX = "PHYS";
    private static final String EBOOK_PREFIX = "EBOOK";
    private static final String SKU_SEPARATOR = "-";

    public static String generatePhysicalBookSku(PhysicalBookEntity physicalBook) {
        if (physicalBook == null || physicalBook.getBook() == null) {
            throw new IllegalArgumentException("PhysicalBookEntity and its book must not be null");
        }
        
        BookEntity book = physicalBook.getBook();
        String bookCode = book.getCode();
        
        if (bookCode == null || bookCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Book code is required to generate SKU");
        }
        
        StringBuilder sku = new StringBuilder();
        sku.append(PHYSICAL_PREFIX)
                .append(SKU_SEPARATOR)
                .append(book.getPublisher().getAcronym())
                .append(SKU_SEPARATOR)
                .append(bookCode.toUpperCase());

        if (physicalBook.getIsbn() != null && !physicalBook.getIsbn().trim().isEmpty()) {
            String isbn = physicalBook.getIsbn().trim();
            String isbnSuffix = isbn.length() >= 4 
                ? isbn.substring(isbn.length() - 4).toUpperCase()
                : isbn.toUpperCase();
            sku.append(SKU_SEPARATOR).append(isbnSuffix);
        }
        
        return sku.toString();
    }

    public static String generateEbookFileSku(EbookEntity ebook) {

        BookEntity book = ebook.getBook();
        
        String bookCode = book.getCode();
        
        StringBuilder sku = new StringBuilder();
        sku.append(EBOOK_PREFIX)
                .append(SKU_SEPARATOR)
                .append(book.getPublisher().getAcronym())
                .append(SKU_SEPARATOR)
                .append(bookCode.toUpperCase());

        return sku.toString();
    }
}
