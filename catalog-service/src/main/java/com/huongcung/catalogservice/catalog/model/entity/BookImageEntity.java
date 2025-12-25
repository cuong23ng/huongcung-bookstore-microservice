package com.huongcung.catalogservice.catalog.model.entity;

import com.huongcung.catalogservice.media.model.entity.ImageEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_image")
@PrimaryKeyJoinColumn(name = "image_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookImageEntity extends ImageEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(name = "position")
    private Integer position;
}
