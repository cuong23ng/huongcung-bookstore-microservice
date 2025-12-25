package com.huongcung.catalogservice.common.dto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse implements Serializable {
    private String errorCode;
    private String message;
    private Object data;
}