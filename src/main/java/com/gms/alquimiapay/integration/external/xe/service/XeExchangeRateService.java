package com.gms.alquimiapay.integration.external.xe.service;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.integration.external.xe.constants.XeApiPath;
import com.gms.alquimiapay.integration.external.xe.payload.ExchangeRateSuccessDTO;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.payload.ErrorResponse;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class XeExchangeRateService
{

    @Value("${third-party.xe.base-url}")
    private String xeBaseUrl;

    private final XeGenericService xeGenericService;
    private final MessageProvider messageProvider;
    private static final Gson JSON = new Gson();


    public BaseResponse getExchangeRate(String amount, String fromCurrency, String toCurrency){
        String completeUrl = xeBaseUrl.concat(XeApiPath.EXCHANGE_RATE);

        BaseResponse baseResponse = new BaseResponse();
        String code;

        Map<String, Object> params = new HashMap<>();
        params.put("from", fromCurrency);
        params.put("to", toCurrency);
        params.put("amount", amount);
        params.put("decimal_places", "2");

        WebResponse webResponse = xeGenericService.getExchangeWithXe(completeUrl, params);
        if(webResponse.isHasConnectionError()){
            ErrorResponse errorResponse = JSON.fromJson(webResponse.getErrorResponseJson(), ErrorResponse.class);
            baseResponse.setResponseCode(errorResponse.getResponseCode());
            baseResponse.setResponseMessage(errorResponse.getResponseMessage());
            return baseResponse;
        }

        ExchangeRateSuccessDTO successDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), ExchangeRateSuccessDTO.class);
        code = ResponseCode.SUCCESS;
        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(messageProvider.getMessage(code));
        baseResponse.setOtherDetails(successDTO);
        baseResponse.setOtherDetailsJson(String.valueOf(successDTO.getTo().get(0).getMid()));

        return baseResponse;
    }

}
