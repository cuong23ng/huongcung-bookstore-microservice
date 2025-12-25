package com.huongcung.catalogservice.media.model.entity;

import com.huongcung.catalogservice.catalog.model.entity.EbookEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ebook_file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EbookFileEntity extends MediaEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book")
    private EbookEntity book;

    @Column(name = "download_count")
    private Integer downloadCount = 0;
}
