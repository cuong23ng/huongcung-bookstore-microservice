package com.huongcung.catalogservice.catalog.model.entity;

import com.huongcung.catalogservice.catalog.enumeration.BookStatus;
import com.huongcung.catalogservice.common.model.BaseEntity;
import com.huongcung.catalogservice.media.model.entity.EbookFileEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ebook")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EbookEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "book_id", nullable = false, unique = true)
    private BookEntity book;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EbookFileEntity> files;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookStatus status = BookStatus.UNPUBLISHED;
}
