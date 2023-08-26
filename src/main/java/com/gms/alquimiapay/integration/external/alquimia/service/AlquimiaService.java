package com.gms.alquimiapay.integration.external.alquimia.service;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.CurrencyCode;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.integration.external.alquimia.dto.data.*;
import com.gms.alquimiapay.integration.external.alquimia.dto.request.ReceiveOrderRequestDTO;
import com.gms.alquimiapay.integration.external.alquimia.dto.response.ReceiveOrderResponseDTO;
import com.gms.alquimiapay.integration.internal.remittance.IIntegrationRemittanceService;
import com.gms.alquimiapay.modules.transaction.constant.HistoryType;
import com.gms.alquimiapay.modules.transaction.model.TransactionEntry;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.payload.ErrorResponse;
import com.gms.alquimiapay.util.BigDecimalUtil;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AlquimiaService implements IIntegrationRemittanceService {

    @Value("${third-party.alquimia.account.account-type}")
    private String poolAccountTypeForAlquimia;

    @Value("${third-party.alquimia.account.account-number}")
    private String poolAccountNumberForAlquimia;

    @Value("${third-party.alquimia.remittance.submit-order-url}")
    private String alquimiaRemittanceSubmissionUrl;

    @Value("${third-party.alquimia.remittance.beneficiary.default-address}")
    private String defaultBeneficiaryAddress;

    @Value("${third-party.alquimia.remittance.beneficiary.default-address}")
    private String defaultBeneficiaryCity;

    private final AlquimiaGenericService alquimiaGenericService;

    private final MessageProvider messageProvider;

    private final static Gson JSON =  new Gson();

    private final static Integer ALQUIMIA_SUCCESS_RESPONSE = 0;


    @Autowired
    public AlquimiaService(AlquimiaGenericService alquimiaGenericService, MessageProvider messageProvider) {
        this.alquimiaGenericService = alquimiaGenericService;
        this.messageProvider = messageProvider;
    }


    @Override
    public BaseResponse processRemittanceSubmission(GmsUser user, TransactionEntry transactionEntry) {
        BaseResponse response = new BaseResponse();
        String code;

        Remittance remittance = this.buildAlquimiaRemittanceEntity(transactionEntry);
        Sender sender = this.buildAlquimiaSenderEntity(user);
        Beneficiary beneficiary = this.buildAlquimiaBeneficiaryEntity(transactionEntry);

        ReceiveOrderRequestDTO requestDTO = ReceiveOrderRequestDTO.builder()
                .user(alquimiaGenericService.getAlquimiaLoginUser())
                .remittance(remittance)
                .sender(sender)
                .beneficiary(beneficiary)
                .build();

        log.info("Remittance RequestDTO: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = alquimiaGenericService.postExchangeWithAlquimia(alquimiaRemittanceSubmissionUrl, requestDTO);
        log.info("WebResponse: {}", JSON.toJson(webResponse));

        if(webResponse.isHasConnectionError()){
            ErrorResponse errorResponse = JSON.fromJson(webResponse.getErrorResponseJson(), ErrorResponse.class);
            response.setResponseCode(errorResponse.getResponseCode());
            response.setResponseMessage(response.getResponseMessage());
            return errorResponse;
        }

        Object[] details = new Object[]{requestDTO.getUser(), remittance, sender, beneficiary};
        response.setOtherDetails(details);
        response.setOtherDetailsJson(webResponse.getSuccessResponseJson());

        ReceiveOrderResponseDTO receiveOrderRequestDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), ReceiveOrderResponseDTO.class);
        AlquimiaBaseResponse alquimiaBaseResponse = receiveOrderRequestDTO.getResponse();

        if(alquimiaBaseResponse.getResponseCode().intValue() == ALQUIMIA_SUCCESS_RESPONSE.intValue()){
            log.info("Customer Beneficiary details submitted to Alquimia successfully");
            code = ResponseCode.SUCCESS;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;    // Return immediately without further checks.
        }

        code = ResponseCode.THIRD_PARTY_CLIENT_FAILURE;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        return response;
    }


    private Remittance buildAlquimiaRemittanceEntity(TransactionEntry transactionEntry){
        String remittanceId = UUID.randomUUID().toString();
        if(remittanceId.length() >= 32)
            remittanceId = remittanceId.substring(0, 32);

        return Remittance.builder()
                .noRemittance(remittanceId)
                .noSequence("418")
                .sendAmount(BigDecimalUtil.from(transactionEntry.getAmountForFeeRequest()))
                .sendCurrency(CurrencyCode.USD.name())
                .exchangeRate(transactionEntry.getExchangeRate())
                .paidAmount(BigDecimalUtil.from(transactionEntry.getDestinationAmount()))
                .paidCurrency(transactionEntry.getDestinationCurrency())
                .sendType(HistoryType.DEPOSIT.name())
                .accountType(poolAccountTypeForAlquimia)
                .accountNumber(poolAccountNumberForAlquimia)
                .build();
    }

    private Sender buildAlquimiaSenderEntity(GmsUser user){
        return Sender.builder()
                .pais(CurrencyCode.USD.name())
                .id(1)
                .firstName(user.getFirstName())
                .secondName(user.getMiddleName())
                .lastName(user.getLastName())
                .secondLastName(user.getLastName())
                .homePhone(user.getMobileNumber())
                .workPhone(user.getMobileNumber())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .email(user.getBusinessEmail() == null || user.getBusinessEmail().isEmpty() || user.getBusinessEmail().isBlank() ? user.getEmailAddress() : user.getBusinessEmail())
                .build();
    }

    private Beneficiary buildAlquimiaBeneficiaryEntity(TransactionEntry entry){
        List<String> benNames = Arrays.stream(entry.getCustomerBeneficiaryName().trim().split(StringValues.SINGLE_EMPTY_SPACE)).filter(s -> !s.isBlank() && !s.isEmpty()).collect(Collectors.toList());
        return Beneficiary.builder()
                .id(1)
                .firstName(benNames.get(0))
                .secondName(StringValues.EMPTY_STRING)
                .lastName(benNames.size() > 1 ? benNames.get(1) : StringValues.EMPTY_STRING)
                .secondLastName(StringValues.EMPTY_STRING)
                .homePhone(entry.getCustomerBeneficiaryPhone())
                .workPhone(entry.getCustomerBeneficiaryPhone())
                .address(defaultBeneficiaryAddress)  // Beneficiary address
                .city(defaultBeneficiaryCity)  // Beneficiary city
                .build();
    }
}
