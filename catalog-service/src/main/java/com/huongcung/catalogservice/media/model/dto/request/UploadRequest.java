package com.huongcung.catalogservice.media.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequest {
    private Integer id; // Start from 1
    private String fileName;
    private String folderPath;
    private String contentType;
}
