package com.huongcung.catalogservice.media.model.entity;

import com.huongcung.catalogservice.catalog.enumeration.ReviewStatus;
import com.huongcung.catalogservice.common.model.BaseEntity;
import com.huongcung.catalogservice.media.enumeration.FileType;
import com.huongcung.catalogservice.media.enumeration.MediaStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class MediaEntity extends BaseEntity {
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type")
    private FileType fileType;

    @Column(name = "url", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MediaStatus status;
}
