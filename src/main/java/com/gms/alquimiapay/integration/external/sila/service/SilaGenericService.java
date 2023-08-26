package com.gms.alquimiapay.integration.external.sila.service;

import com.gms.alquimiapay.constants.Creator;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.dao.GmsDAO;
import com.gms.alquimiapay.integration.external.sila.constant.Crypto;
import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaAuthHeader;
import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaBusinessHeader;
import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaIdentityHeader;
import com.gms.alquimiapay.integration.external.sila.dto.auth.request.AuthTokenRequestDTO;
import com.gms.alquimiapay.integration.external.sila.dto.auth.response.AuthTokenResponseDTO;
import com.gms.alquimiapay.model.GmsParam;
import com.gms.alquimiapay.payload.ErrorResponse;
import com.gms.alquimiapay.web.WebClient;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SilaGenericService implements ISilaGenericService {

    @Value("${third-party.sila.token-url}")
    private String tokenUrl;

    @Value("${third-party.sila.version}")
    private String silaVersion;

    @Value("${third-party.sila.app-handle}")
    private String silaAuthHandle;

    @Value("${third-party.sila.client-id}")
    private String silaClientId;

    @Value("${third-party.sila.client-secret}")
    private String silaClientSecret;

    @Value("${third-party.sila.access-token.expiration-minutes}")
    private String expirationMin;

    @Value("${third-party.sila.base-url}")
    private String silaBaseUrl;

    @Autowired
    private GmsDAO gmsDAO;

    @Autowired
    private WebClient webClient;

    private static final Gson JSON = new Gson();



    @Override
    public String getSilaAuthToken(){

        GmsParam gmsParam = gmsDAO.getParamByKey("SILA_ACCESS_TOKEN");
        if(gmsParam == null){
            WebResponse webResponse = connectForSilaAuthToken();
            if(!webResponse.isHasConnectionError()){
                AuthTokenResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), AuthTokenResponseDTO.class);
                gmsParam = new GmsParam();
                gmsParam.setParamKey("SILA_ACCESS_TOKEN");
                gmsParam.setParamValue(responseDTO.getAccessToken().getToken());
                gmsParam.setParamDesc("Sila Access Token");
                gmsParam.setCreatedAt(LocalDateTime.now().toString());
                gmsParam.setCreatedBy(Creator.SYSTEM.name());
                gmsParam.setUpdatedAt(LocalDateTime.now().toString());
                gmsParam.setUpdatedBy(Creator.SYSTEM.name());
                gmsParam.setExpirationAt(getParsableDateString(responseDTO.getAccessToken().getExpirationAt()));
                gmsParam.setOtherInfoLogs(JSON.toJson(responseDTO));
                GmsParam savedParam = gmsDAO.saveParam(gmsParam);
                return savedParam.getParamValue();
            }

            log.info("Error while connecting to get Sila Access token: {}", webResponse.getErrorResponseJson());
        }
        else{
            String expirationString = gmsParam.getExpirationAt();
            int offset = Integer.parseInt(expirationMin);
            if(isDateStringNotExpiredByOffset(expirationString, offset)){
                return gmsParam.getParamValue();
            }
            WebResponse webResponse = connectForSilaAuthToken();
            if(!webResponse.isHasConnectionError()){
                AuthTokenResponseDTO authTokenResponseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), AuthTokenResponseDTO.class);
                gmsParam.setParamValue(authTokenResponseDTO.getAccessToken().getToken());
                gmsParam.setExpirationAt(getParsableDateString(authTokenResponseDTO.getAccessToken().getExpirationAt()));
                GmsParam updatedParam = gmsDAO.updateParam(gmsParam);

                log.info("Sila Access token refreshed: {}", authTokenResponseDTO);
                return updatedParam.getParamValue();
            }
        }

        return StringValues.EMPTY_STRING;

    }

    @Bean
    public WebResponse connectForSilaAuthToken(){
        WebResponse webResponse = new WebResponse();

        SilaAuthHeader silaHeader = new SilaAuthHeader();
        silaHeader.setCreated(System.currentTimeMillis() - 10_000);
        silaHeader.setReference(UUID.randomUUID().toString());
        silaHeader.setVersion(silaVersion);
        silaHeader.setAuthHandle(silaAuthHandle);

        AuthTokenRequestDTO requestDTO = new AuthTokenRequestDTO();
        requestDTO.setHeader(silaHeader);
        Map<String, String> header = new HashMap<>();
        header.put(StringValues.AUTH_HEADER_KEY, getSilaBasicAuthHeader());

        log.info("Request Header: {}", header.toString());
        log.info("RequestJson: {}", JSON.toJson(requestDTO));

        String serviceResponseJson = webClient.postForObject(tokenUrl, requestDTO, header, null);
        log.info("ResponseJsonPost: {}", serviceResponseJson);

        AuthTokenResponseDTO responseDTO = JSON.fromJson(serviceResponseJson, AuthTokenResponseDTO.class);
        if(responseDTO.isSuccess()){
            webResponse.setHasConnectionError(false);
            webResponse.setSuccessResponseJson(serviceResponseJson);
        }else{
            webResponse.setErrorResponseJson(serviceResponseJson);
        }

        return webResponse;
    }

    @Override
    public SilaIdentityHeader getSilaIdentityHeader(){
        SilaIdentityHeader silaIdentityHeader = new SilaIdentityHeader();
        silaIdentityHeader.setAppHandle(silaAuthHandle);
        silaIdentityHeader.setCreated(System.currentTimeMillis() - 10_000);
        silaIdentityHeader.setVersion(silaVersion);
        silaIdentityHeader.setCrypto(Crypto.ETH.name());
        silaIdentityHeader.setReference(UUID.randomUUID().toString());
        return silaIdentityHeader;
    }

    @Override
    public SilaBusinessHeader getSilaBusinessHeader(){
        SilaBusinessHeader silaBusinessHeader = new SilaBusinessHeader();
        silaBusinessHeader.setAppHandle(silaAuthHandle);
        silaBusinessHeader.setCreated(System.currentTimeMillis() - 10_000);
        silaBusinessHeader.setVersion(silaVersion);
        silaBusinessHeader.setCrypto(Crypto.ETH.name());
        silaBusinessHeader.setReference(UUID.randomUUID().toString());
        return silaBusinessHeader;
    }

    private String getSilaBasicAuthHeader(){
        String credentialConcat = String.join(StringValues.COLON, silaClientId, silaClientSecret);
        String base64 = Base64.getEncoder().encodeToString(credentialConcat.getBytes(StandardCharsets.UTF_8));
        return StringValues.AUTH_HEADER_BASIC_KEY.concat(base64);
    }

    private Map<String, String> getSilaBearerHeader(){
        Map<String, String> headers = new HashMap<>();
        headers.put(StringValues.AUTH_HEADER_KEY, StringValues.AUTH_HEADER_BEARER_KEY.concat(getSilaAuthToken()));
        return headers;
    }

    @Override
    public WebResponse getExchangeWithSila(String url, Map<String, Object> params) {
        WebResponse webResponse = new WebResponse();

        String responseJson = webClient.getForObject(url, getSilaBearerHeader(), params);
        GenericSilaResponseDTO genericSilaResponseDTO = JSON.fromJson(responseJson, GenericSilaResponseDTO.class);
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
    public WebResponse getExchangeWithSila(String url) {
        return getExchangeWithSila(url, null);
    }


    @Override
    public WebResponse postExchangeWithSila(String url, Object requestObject, Map<String, Object> params) {
        WebResponse webResponse = new WebResponse();

        String responseJson = webClient.postForObject(url, requestObject, getSilaBearerHeader(), params);
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
    public WebResponse postExchangeWithSila(String url, Object requestObject) {
        return postExchangeWithSila(url, requestObject, null);
    }

    @Override
    public WebResponse postFormExchangeWithSila(String url, Map<String, Object> formData) {
        WebResponse webResponse = new WebResponse();
        String responseJson = webClient.postForForm(url, formData, getSilaBearerHeader());
        GenericSilaResponseDTO genericSilaResponseDTO = JSON.fromJson(responseJson, GenericSilaResponseDTO.class);
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
    public WebResponse postFormExchangeWithSilaRestTemplate(String url, Map<String, Object> formData) {
        WebResponse webResponse = new WebResponse();
        String responseJson = webClient.postForFormRestTemplate(url, formData, getSilaBearerHeader());
        GenericSilaResponseDTO genericSilaResponseDTO = JSON.fromJson(responseJson, GenericSilaResponseDTO.class);
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
    public String resolveSilaUrl(String relativeUrl) {
        return silaBaseUrl.concat(relativeUrl);
    }

    @Override
    public String resolveSilaMessage(String internalMessage, String silaMessage) {
        return internalMessage.concat(StringValues.COLON).concat(StringValues.SINGLE_EMPTY_SPACE).concat(silaMessage == null ? StringValues.SINGLE_EMPTY_SPACE : silaMessage);
    }

    private boolean isDateStringNotExpiredByOffset(String dateString, int offset){
        LocalDateTime expirationDateTime = LocalDateTime.parse(dateString);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutOffTime = expirationDateTime.minusMinutes(offset);
        return cutOffTime.isAfter(now) || cutOffTime.isEqual(now);
    }

    private String getParsableDateString(@NonNull String dateTimeString){
        dateTimeString = dateTimeString.trim();
        char lastChar = dateTimeString.charAt(dateTimeString.length() - 1);
        if(!Character.isDigit(lastChar))
            return dateTimeString.substring(0, dateTimeString.length() - 1);
        else
            return dateTimeString;
    }
}
