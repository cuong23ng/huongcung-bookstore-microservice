package com.huongcung.catalogservice.catalog.model.dto;

import com.huongcung.catalogservice.media.model.dto.ImageDTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthorDTO {
    private Long id;
    private String name;
    private String biography;
    private ImageDTO image;
    private LocalDate birthDate;
    private String nationality;
}
