package com.huongcung.catalogservice.catalog.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageBase64Upload {

    private Integer id;

    private String fileName;

    private String fileType;

    @NotBlank(message = "Image data is required")
    private String base64Data;
}
