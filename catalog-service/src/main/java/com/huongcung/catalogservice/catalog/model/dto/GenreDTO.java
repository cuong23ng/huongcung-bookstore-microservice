package com.huongcung.catalogservice.catalog.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenreDTO {
    private String code;
    private String description;
    private GenreDTO parent;
    private List<GenreDTO> children;
}
