package com.gms.alquimiapay.integration.external.alquimia.service;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.integration.external.alquimia.dto.data.AlquimiaUser;
import com.gms.alquimiapay.payload.ErrorResponse;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlquimiaGenericServiceImpl implements AlquimiaGenericService
{

    @Value("${third-party.alquimia.user.login}")
    private String login;

    @Value("${third-party.alquimia.user.password}")
    private String password;

    private static final Gson JSON = new Gson();

    private final MessageProvider messageProvider;


    @Autowired
    public AlquimiaGenericServiceImpl(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    @Override
    public AlquimiaUser getAlquimiaLoginUser() {
        return AlquimiaUser.builder()
                .login(login)
                .password(password)
                .build();
    }


    @Override
    public WebResponse postExchangeWithAlquimia(String url, Object requestBody) {
        HttpResponse<String> httpResponse;
        String responseJson;
        String code;

        WebResponse webResponse = new WebResponse();
        webResponse.setHasConnectionError(true);
        webResponse.setSuccessResponseJson(null);

        ErrorResponse errorResponse = ErrorResponse.getInstance();

        try{
            httpResponse = Unirest.post(url)
                    .contentType("application/json")
                    .body(JSON.toJson(requestBody))
                    .asString();
            if(httpResponse != null){
                responseJson = httpResponse.getBody();
                if(responseJson != null && !responseJson.isEmpty() && !responseJson.isBlank()){
                    webResponse.setErrorResponseJson(null);
                    webResponse.setHasConnectionError(false);
                    webResponse.setSuccessResponseJson(responseJson);
                    return webResponse;  // Return immediately without further checks.
                }else{
                    code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
                    errorResponse.setResponseCode(code);
                    errorResponse.setResponseMessage(messageProvider.getMessage(code));
                    responseJson = JSON.toJson(errorResponse);
                }
            }else{
                log.info("Alquimia Third-Party Web service is currently unavailable.");
                code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
                errorResponse.setResponseCode(code);
                errorResponse.setResponseMessage(messageProvider.getMessage(code));
                responseJson = JSON.toJson(errorResponse);
            }
        }catch (Exception e){
            log.info("Exception occurred while trying to connect to the third party: {}", e.getMessage());
            code = ResponseCode.SYSTEM_ERROR;
            errorResponse.setResponseCode(code);
            errorResponse.setResponseMessage(e.getMessage());
            responseJson = JSON.toJson(errorResponse);
        }

        webResponse.setErrorResponseJson(responseJson);
        return webResponse;
    }

}
