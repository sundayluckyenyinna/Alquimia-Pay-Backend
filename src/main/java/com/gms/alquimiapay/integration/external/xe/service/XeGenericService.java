package com.gms.alquimiapay.integration.external.xe.service;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.payload.ErrorResponse;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class XeGenericService {

    @Value("${third-party.xe.app-id}")
    private String xeAppId;

    @Value("${third-party.xe.app-key}")
    private String xeAppKey;

    private final MessageProvider messageProvider;

    private static final Gson JSON = new Gson();


    public WebResponse getExchangeWithXe(String url, Map<String, Object> params){
        HttpResponse<String> httpResponse;
        String responseJson;
        String code;
        ErrorResponse errorResponse = ErrorResponse.getInstance();

        WebResponse webResponse = new WebResponse();
        webResponse.setHasConnectionError(false);
        Unirest.config().verifySsl(false);
        try{
            GetRequest getRequest = Unirest
                    .get(url)
                    .headers(getXeBasicAuthHeader());
            if(params != null){
                getRequest = getRequest.queryString(params);
            }
            httpResponse = getRequest.asString();
            if(httpResponse.isSuccess()){
                log.info("Successful connection with the XE Exchange Rate web service: {}", httpResponse.getBody());
                responseJson = httpResponse.getBody();
                webResponse.setSuccessResponseJson(responseJson);
            }else{
                log.info("Could not get successful response with the XE Exchange Rate Web service: {}", httpResponse.getBody());

                code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
                errorResponse.setResponseCode(code);
                errorResponse.setResponseMessage(messageProvider.getMessage(code));
                responseJson = JSON.toJson(errorResponse);

                webResponse.setHasConnectionError(true);
                webResponse.setErrorResponseJson(responseJson);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("Internal system encountered while calling the XE Exchange Rate Web service: {}", e.getMessage());

            errorResponse.setResponseCode(ResponseCode.SYSTEM_ERROR);
            errorResponse.setResponseMessage(e.getMessage());
            responseJson = JSON.toJson(errorResponse);
            webResponse.setErrorResponseJson(responseJson);
        }

        return webResponse;
    }


    private Map<String, String> getXeBasicAuthHeader(){
        String credentialsConcat = xeAppId.concat(StringValues.COLON).concat(xeAppKey);
        String base64 = Base64.getEncoder().encodeToString(credentialsConcat.getBytes(StandardCharsets.UTF_8));

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        header.put(StringValues.AUTH_HEADER_KEY, StringValues.AUTH_HEADER_BASIC_KEY.concat(base64));

        return header;
    }
}
