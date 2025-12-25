package com.huongcung.catalogservice.catalog.model.entity;

import com.huongcung.catalogservice.common.model.BaseEntity;
import com.huongcung.catalogservice.media.model.entity.ImageEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "author")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image")
    private ImageEntity image;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "nationality")
    private String nationality;
}
