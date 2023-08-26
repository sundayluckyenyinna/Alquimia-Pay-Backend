package com.gms.alquimiapay.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse
{
    private String responseCode;
    private String responseMessage;
    private Object otherDetails;
    private String otherDetailsJson;
}
