package com.huongcung.paymentservice.common.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse implements Serializable {
    private String errorCode;
    private String message;
    private Object data;
}
