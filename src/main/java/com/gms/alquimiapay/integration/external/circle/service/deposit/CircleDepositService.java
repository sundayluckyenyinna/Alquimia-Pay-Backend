package com.gms.alquimiapay.integration.external.circle.service.deposit;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.QualifierValue;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.integration.external.circle.constant.CircleApiPath;
import com.gms.alquimiapay.integration.external.circle.dto.CircleErrorResponse;
import com.gms.alquimiapay.integration.external.circle.dto.deposit.response.CircleBankDepositResponseDTO;
import com.gms.alquimiapay.integration.external.circle.service.ICircleGenericService;
import com.gms.alquimiapay.integration.internal.deposit.IIntegrationDepositService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = QualifierValue.CIRCLE_PARTY_DEPOSIT_SERVICE)
public class CircleDepositService implements IIntegrationDepositService
{

    @Autowired
    private ICircleGenericService genericService;
    @Autowired
    private MessageProvider messageProvider;

    private static final Gson JSON = new Gson();


    @Override
    public BaseResponse processFetchAllBankDeposit(String startDateTime) {
        BaseResponse response = new BaseResponse();
        String code = ResponseCode.SYSTEM_ERROR;

        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));

        String url = genericService.resolveCircleApiPath(CircleApiPath.WIRE_DEPOSITS);
        if(!startDateTime.equalsIgnoreCase(StringValues.EMPTY_STRING))
            url = url.concat(StringValues.QUESTION_MARK).concat("from=").concat(startDateTime);

        log.info("URL to fetch all bank deposits: {}", url);

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

        log.info("ResponseJson to Get all deposits: {}", webResponse.getSuccessResponseJson());
        CircleBankDepositResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), CircleBankDepositResponseDTO.class);

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        response.setOtherDetails(responseDTO);

        return response;
    }
}
