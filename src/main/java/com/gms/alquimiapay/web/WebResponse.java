package com.gms.alquimiapay.web;

import lombok.Data;

@Data
public class WebResponse
{
    private boolean hasConnectionError = true;
    private String successResponseJson = null;
    private String errorResponseJson = null;
}
