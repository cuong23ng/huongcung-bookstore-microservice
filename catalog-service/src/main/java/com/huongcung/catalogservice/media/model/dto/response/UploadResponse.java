package com.huongcung.catalogservice.media.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UploadResponse {
    private Integer id;
    private String key;
    private String uploadUrl;
}
