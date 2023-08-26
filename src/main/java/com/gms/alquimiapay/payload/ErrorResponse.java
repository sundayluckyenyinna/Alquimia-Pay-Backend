package com.gms.alquimiapay.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ErrorResponse extends BaseResponse
{
    private ErrorResponse(){}
    public static ErrorResponse getInstance(){
        return new ErrorResponse();
    }
}
