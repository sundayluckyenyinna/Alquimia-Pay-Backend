package com.gms.alquimiapay.integration.external.circle.service.account;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.QualifierValue;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.integration.external.circle.constant.CircleApiPath;
import com.gms.alquimiapay.integration.external.circle.dto.CircleErrorResponse;
import com.gms.alquimiapay.integration.external.circle.dto.account.data.CircleAccountBillingDetails;
import com.gms.alquimiapay.integration.external.circle.dto.account.data.CircleBankAddress;
import com.gms.alquimiapay.integration.external.circle.dto.account.request.CircleCreateVirtualAccountRequestDTO;
import com.gms.alquimiapay.integration.external.circle.dto.account.response.CircleCreateWireAccountInstructionResponseDTO;
import com.gms.alquimiapay.integration.external.circle.dto.account.response.CircleLinkWireAccountResponseDTO;
import com.gms.alquimiapay.integration.external.circle.service.ICircleGenericService;
import com.gms.alquimiapay.integration.internal.account.IAccountIntegrationService;
import com.gms.alquimiapay.model.MasterBankAccount;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service(value = QualifierValue.CIRCLE_PARTY_ACCOUNT_SERVICE)
public class CircleAccountService implements IAccountIntegrationService
{

    @Autowired
    private ICircleGenericService genericService;
    @Autowired
    private MessageProvider messageProvider;

    private static final Gson JSON = new Gson();


    @Override
    public BaseResponse processLinkWireAccount(MasterBankAccount masterBankAccount) {
        BaseResponse response = new BaseResponse();
        String code;

        String url = genericService.resolveCircleApiPath(CircleApiPath.WIRE_ACCOUNT_LINKING);
        CircleCreateVirtualAccountRequestDTO requestDTO = new CircleCreateVirtualAccountRequestDTO();
        requestDTO.setAccountNumber(masterBankAccount.getAccountNumber());
        requestDTO.setIdempotencyKey(UUID.randomUUID().toString());
        requestDTO.setRoutingNumber(masterBankAccount.getRoutingNumber());

        CircleAccountBillingDetails billingDetails = JSON.fromJson(masterBankAccount.getBillingDetailsJson(), CircleAccountBillingDetails.class);
        CircleBankAddress bankAddress = JSON.fromJson(masterBankAccount.getBankAddressJson(), CircleBankAddress.class);
        requestDTO.setBillingDetails(billingDetails);
        requestDTO.setBankAddress(bankAddress);

        log.info("RequestJson to create virtual account: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = genericService.postExchangeWithCircle(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));

            log.info("Circle service unavailable");
            return response;
        }

        CircleErrorResponse errorResponse = JSON.fromJson(webResponse.getSuccessResponseJson(), CircleErrorResponse.class);
        if(errorResponse.getCode() != null){
            code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            response.setResponseCode(code);
            response.setResponseMessage(errorResponse.getMessage());

            log.info("Circle third-party failure Json: {}", webResponse.getSuccessResponseJson());
            return response;
        }

        log.info("ResponseJson to link wire account: {}", webResponse.getSuccessResponseJson());

        String dataJson = genericService.getCircleDataJson(webResponse.getSuccessResponseJson());
        CircleLinkWireAccountResponseDTO responseDTO = JSON.fromJson(dataJson, CircleLinkWireAccountResponseDTO.class);
        Object[] data = new Object[]{responseDTO, requestDTO.getIdempotencyKey()};

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        response.setOtherDetails(data);
        return response;
    }


    @Override
    public BaseResponse processCreateCustomerVirtualAccountInstruction(String id){
        BaseResponse response = new BaseResponse();
        String code;

        String url = genericService.resolveCircleApiPath(CircleApiPath.WIRE_VIRTUAL_ACCOUNT_INSTRUCTION);
        url = url.replace("{id}", id);

        log.info("Wire account instruction URL: {}", url);

        WebResponse webResponse = genericService.getExchangeWithCircle(url);
        if(webResponse.isHasConnectionError()){
            code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));

            log.info("Circle service unavailable");
            return response;
        }

        CircleErrorResponse errorResponse = JSON.fromJson(webResponse.getSuccessResponseJson(), CircleErrorResponse.class);
        if(errorResponse.getCode() != null){
            code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            response.setResponseCode(code);
            response.setResponseMessage(errorResponse.getMessage());

            log.info("Circle third-party failure Json: {}", webResponse.getSuccessResponseJson());
            return response;
        }

        log.info("ResponseJson to create customer account instruction: {}", webResponse.getSuccessResponseJson());

        String dataJson = genericService.getCircleDataJson(webResponse.getSuccessResponseJson());
        CircleCreateWireAccountInstructionResponseDTO responseDTO = JSON.fromJson(dataJson, CircleCreateWireAccountInstructionResponseDTO.class);

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        response.setOtherDetails(responseDTO);
        response.setOtherDetailsJson(dataJson);
        return response;
    }

    @Override
    public BaseResponse processGetSingleAccountStatusRequest(String id){
        BaseResponse response = new BaseResponse();
        String code;

        String url = genericService.resolveCircleApiPath(CircleApiPath.WIRE_SINGLE_ACCOUNT);
        url = url.replace("{id}", id);

        log.info("Wire account instruction URL: {}", url);

        WebResponse webResponse = genericService.getExchangeWithCircle(url);
        if(webResponse.isHasConnectionError()){
            code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));

            log.info("Circle service unavailable");
            return response;
        }

        CircleErrorResponse errorResponse = JSON.fromJson(webResponse.getSuccessResponseJson(), CircleErrorResponse.class);
        if(errorResponse.getCode() != null){
            code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            response.setResponseCode(code);
            response.setResponseMessage(errorResponse.getMessage());

            log.info("Circle third-party failure Json: {}", webResponse.getSuccessResponseJson());
            return response;
        }

        log.info("ResponseJson to create customer account instruction: {}", webResponse.getSuccessResponseJson());

        String dataJson = genericService.getCircleDataJson(webResponse.getSuccessResponseJson());
        JSONObject jsonObject = new JSONObject(dataJson);
        String status = ((String) jsonObject.get("status")).toUpperCase();

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        response.setOtherDetails(status);
        response.setOtherDetailsJson(status);

        return response;
    }
}
