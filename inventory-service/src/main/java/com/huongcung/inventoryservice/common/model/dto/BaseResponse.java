package com.huongcung.inventoryservice.common.model.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse implements Serializable {
    private String errorCode;
    private String message;
    private Object data;
}