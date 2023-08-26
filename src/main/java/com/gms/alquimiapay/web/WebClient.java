package com.gms.alquimiapay.web;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.payload.ErrorResponse;
import com.google.gson.Gson;
import kong.unirest.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;

@Slf4j
@Component
public class WebClient implements IWebClient
{

    @Autowired
    private MessageProvider messageProvider;

    private static final Gson JSON = new Gson();


    @Override
    public String getForObject(String url, Map<String, String> headers, Map<String, Object> params) {
        String responseJson;
        HttpResponse<String> response;
        ErrorResponse errorResponse = ErrorResponse.getInstance();
        Unirest.config().verifySsl(false);
        try{
            GetRequest getRequest = Unirest.get(url)
                    .header("Content-Type", "application/json");
            if(headers != null)
                getRequest = getRequest.headers(headers);
            if(params != null)
                getRequest = getRequest.queryString(params);
            response = getRequest.asString();

            if(response != null){
                responseJson = response.getBody();
                log.info("HttpConnection Success: {} GET", responseJson);
            }else{
                log.info("HttpConnection Service Unavailable GET: {}", "Third party service unavailable");
                String responseCode = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
                errorResponse.setResponseCode(responseCode);
                errorResponse.setResponseMessage(messageProvider.getMessage(responseCode));
                responseJson = JSON.toJson(errorResponse);
            }
        }catch (UnirestException ex){
            log.error("Internal Server error while performing HttpConnection GET: {}", ex.getMessage());
            ex.printStackTrace();
            String responseCode = ResponseCode.SYSTEM_ERROR;
            errorResponse.setResponseCode(responseCode);
            errorResponse.setResponseMessage(messageProvider.getMessage(responseCode));
            responseJson = JSON.toJson(errorResponse);
        }
        return responseJson;
    }


    @Override
    public String getForObject(String url, Map<String, String> headers){
        return getForObject(url, headers, null);
    }

    @Override
    public String getForObject(String url) {
        return getForObject(url, null, null);
    }

    @Override
    public String postForObject(String url, String requestJson, Map<String, String> headers, Map<String, Object> params) {
        String responseJson;
        HttpResponse<String> response;
        ErrorResponse errorResponse = ErrorResponse.getInstance();
        Unirest.config().verifySsl(false);
        try{
            RequestBodyEntity postRequest = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .body(requestJson);
            if(headers != null)
                postRequest = postRequest.headers(headers);
            if(params != null)
                postRequest = postRequest.queryString(params);
            response = postRequest.asString();

            if(response != null){
                responseJson = response.getBody();
                log.info("HttpConnection Success POST: {}", responseJson);
            }else{
                log.info("HttpConnection Service Unavailable POST: {}", "Third party service unavailable");
                String responseCode = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
                errorResponse.setResponseCode(responseCode);
                errorResponse.setResponseMessage(messageProvider.getMessage(responseCode));
                responseJson = JSON.toJson(errorResponse);
            }
        }catch (UnirestException ex){
            log.error("Internal Server error while performing HttpConnection POST: {}", ex.getMessage());
            ex.printStackTrace();
            String responseCode = ResponseCode.SYSTEM_ERROR;
            errorResponse.setResponseCode(responseCode);
            errorResponse.setResponseMessage(messageProvider.getMessage(responseCode));
            responseJson = JSON.toJson(errorResponse);
        }
        return responseJson;
    }

    @Override
    public String postForObject(String url, Object requestObject, Map<String, String> headers, Map<String, Object> params) {
        String requestJson = JSON.toJson(requestObject);
        return postForObject(url, requestJson, headers, params);
    }

    @Override
    public String postForObject(String url, Object requestObject) {
        return postForObject(url, requestObject, null, null);
    }

    @Override
    public String postForForm(String url, Map<String, Object> formData, Map<String, String> headers){
        String responseJson;
        HttpResponse<String> response;
        ErrorResponse errorResponse = ErrorResponse.getInstance();
        Unirest.config().verifySsl(false);
        try{
            MultipartBody postRequest = Unirest.post(url)
                    .fields(formData);

            if(headers != null)
                postRequest = postRequest.headers(headers);

            response = postRequest.asString();

            if(response != null){
                responseJson = response.getBody();
                log.info("HttpConnection Success POST: {}", responseJson);
            }else{
                log.info("HttpConnection Service Unavailable POST: {}", "Third party service unavailable");
                String responseCode = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
                errorResponse.setResponseCode(responseCode);
                errorResponse.setResponseMessage(messageProvider.getMessage(responseCode));
                responseJson = JSON.toJson(errorResponse);
            }
        }catch (UnirestException ex){
            log.error("Internal Server error while performing HttpConnection POST: {}", ex.getMessage());
            ex.printStackTrace();
            String responseCode = ResponseCode.SYSTEM_ERROR;
            errorResponse.setResponseCode(responseCode);
            errorResponse.setResponseMessage(messageProvider.getMessage(responseCode));
            responseJson = JSON.toJson(errorResponse);
        }
        return responseJson;
    }

    @Override
    public String postForFormRestTemplate(String url, Map<String, Object> formData, Map<String, String> headers){
        org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        for(Map.Entry<String, String> entry: headers.entrySet()){
            httpHeaders.add(entry.getKey(), entry.getValue());
        }

        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        File file = (File) formData.get("file");
        String dataJson = String.valueOf(formData.get("data"));
        Resource resource = new FileSystemResource(file.getAbsolutePath());

        builder.part("file", resource, MediaType.IMAGE_JPEG);
        builder.part("data", dataJson);

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, HttpEntity<?>> map = builder.build();

        HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity = new HttpEntity<>(map, httpHeaders);
        ErrorResponse errorResponse = ErrorResponse.getInstance();
        ResponseEntity<String> responseEntity;
        String responseJson;
        try{
            responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
            if(responseEntity.getBody() != null){
                responseJson = responseEntity.getBody();
                log.info("HttpConnection Success POST: {}", responseJson);
            }else{
                errorResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE);
                errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
                responseJson = JSON.toJson(errorResponse);
                log.info("HttpConnection Service Unavailable POST: {}", "Third party service unavailable");
            }
        }catch (Exception ex){
            log.error("Internal Server error while performing HttpConnection POST: {}", ex.getMessage());
            ex.printStackTrace();
            String responseCode = ResponseCode.SYSTEM_ERROR;
            errorResponse.setResponseCode(responseCode);
            errorResponse.setResponseMessage(messageProvider.getMessage(responseCode));
            responseJson = ((HttpClientErrorException)ex).getResponseBodyAsString();
        }
        return responseJson;
    }
}
