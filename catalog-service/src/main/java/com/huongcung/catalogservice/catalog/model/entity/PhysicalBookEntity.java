package com.huongcung.catalogservice.catalog.model.entity;

import com.huongcung.catalogservice.catalog.enumeration.BookStatus;
import com.huongcung.catalogservice.common.model.BaseEntity;
import com.huongcung.catalogservice.catalog.enumeration.CoverType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "physical_book")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhysicalBookEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "book_id", nullable = false, unique = true)
    private BookEntity book;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "cover_type")
    private CoverType coverType;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(name = "height_cm")
    private Integer heightCm;

    @Column(name = "width_cm")
    private Integer widthCm;

    @Column(name = "length_cm")
    private Integer lengthCm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookStatus status = BookStatus.UNPUBLISHED;
}
