package com.gms.alquimiapay.integration.external.circle.service;

import com.gms.alquimiapay.constants.Creator;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.dao.GmsDAO;
import com.gms.alquimiapay.model.GmsParam;
import com.gms.alquimiapay.payload.ErrorResponse;
import com.gms.alquimiapay.web.WebClient;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import kong.unirest.json.JSONObject;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CircleGenericService implements ICircleGenericService{

    @Autowired
    private Environment env;
    @Autowired
    GmsDAO gmsDAO;
    @Autowired
    private WebClient webClient;

    private static final Gson JSON = new Gson();

    @Override
    public String getCircleApiKey() {
        GmsParam param = gmsDAO.getParamByKey("CIRCLE_API_KEY");
        if(param == null){
            param = new GmsParam();
            param.setParamValue(env.getProperty("third-party.circle.api-key"));
            param.setParamKey("CIRCLE_API_KEY");
            param.setExpirationAt(null);
            param.setCreatedAt(LocalDateTime.now().toString());
            param.setUpdatedBy(Creator.SYSTEM.name());
            param.setCreatedBy(Creator.SYSTEM.name());
            param.setParamDesc("API key for the Circle web service");
            param.setOtherInfoLogs(StringValues.EMPTY_STRING);
            gmsDAO.saveParam(param);
        }
        return param.getParamValue().trim();
    }

    @Override
    public String resolveCircleApiPath(@NonNull String relativeUrl) {
        String circleBaseUrl = Objects.requireNonNull(env.getProperty("third-party.circle.base-url"));
        return circleBaseUrl.concat(relativeUrl);
    }

    @Override
    public Map<String, String> getCircleAuthHeader(){
        Map<String, String> header = new HashMap<>();
        String authHeaderValue = StringValues.AUTH_HEADER_BEARER_KEY.concat(getCircleApiKey());
        header.put(StringValues.AUTH_HEADER_KEY, authHeaderValue);
        return header;
    }

    @Override
    public WebResponse postExchangeWithCircle(String url, Object requestBody) {
        WebResponse webResponse = new WebResponse();

        String responseJson = webClient.postForObject(url, requestBody, getCircleAuthHeader(), null);
        ErrorResponse errorResponse = JSON.fromJson(responseJson, ErrorResponse.class);
        boolean hasErrorCode = errorResponse.getResponseCode() != null;
        if(hasErrorCode){
            webResponse.setHasConnectionError(true);
            webResponse.setErrorResponseJson(JSON.toJson(errorResponse));
        }else{
            webResponse.setHasConnectionError(false);
            webResponse.setSuccessResponseJson(responseJson);
        }
        return webResponse;
    }

    @Override
    public WebResponse getExchangeWithCircle(String url) {
        WebResponse webResponse = new WebResponse();

        String responseJson = webClient.getForObject(url, getCircleAuthHeader(), null);
        ErrorResponse errorResponse = JSON.fromJson(responseJson, ErrorResponse.class);
        boolean hasErrorCode = errorResponse.getResponseCode() != null;
        if(hasErrorCode){
            webResponse.setHasConnectionError(true);
            webResponse.setErrorResponseJson(JSON.toJson(errorResponse));
        }else{
            webResponse.setHasConnectionError(false);
            webResponse.setSuccessResponseJson(responseJson);
        }
        return webResponse;
    }

    @Override
    public String getCircleDataJson(String webJson){
        JSONObject jsonObject = new JSONObject(webJson);
        JSONObject data = jsonObject.getJSONObject("data");
        return data.toString();
    }
}
