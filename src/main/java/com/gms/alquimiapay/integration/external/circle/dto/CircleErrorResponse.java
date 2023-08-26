package com.gms.alquimiapay.integration.external.circle.dto;

import lombok.Data;

@Data
public class CircleErrorResponse
{
    private Integer code;
    private String message;
}
