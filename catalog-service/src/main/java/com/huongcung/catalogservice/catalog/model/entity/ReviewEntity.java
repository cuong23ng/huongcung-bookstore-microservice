package com.huongcung.catalogservice.catalog.model.entity;

import com.huongcung.catalogservice.common.model.BaseEntity;
import com.huongcung.catalogservice.catalog.enumeration.ReviewStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "review")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private StaffEntity user;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "title", nullable = true)
    private String title;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "is_ai_generated")
    private Boolean isAiGenerated = false;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "review_sources", joinColumns = @JoinColumn(name = "review_id"))
    private List<ReviewSource> sources;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReviewStatus status = ReviewStatus.DRAFT;
}
