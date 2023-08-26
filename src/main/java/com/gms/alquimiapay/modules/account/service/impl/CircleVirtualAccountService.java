package com.gms.alquimiapay.modules.account.service.impl;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.QualifierValue;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.dao.GmsDAO;
import com.gms.alquimiapay.integration.external.circle.dto.account.response.CircleCreateWireAccountInstructionResponseDTO;
import com.gms.alquimiapay.integration.external.circle.dto.account.response.CircleLinkWireAccountResponseDTO;
import com.gms.alquimiapay.integration.internal.account.IAccountIntegrationService;
import com.gms.alquimiapay.model.MasterBankAccount;
import com.gms.alquimiapay.modules.account.model.VirtualAccountCache;
import com.gms.alquimiapay.modules.account.payload.data.AccountBillingDetails;
import com.gms.alquimiapay.modules.account.repository.IAccountRepository;
import com.gms.alquimiapay.modules.account.service.IAccountService;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.kyc.constant.Vendor;
import com.gms.alquimiapay.modules.user.constants.UserType;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.payload.BaseResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service(value = QualifierService.CIRCLE_ACCOUNT_SERVICE)
public class CircleVirtualAccountService implements IAccountService {

    private final IAccountIntegrationService accountIntegrationService;
    private final GmsDAO gmsDAO;
    private final IUserRepository userRepository;
    private final IAccountRepository accountRepository;
    private final MessageProvider messageProvider;

    private static final Gson JSON = new Gson();


    @Autowired
    public CircleVirtualAccountService(
            @Qualifier(QualifierValue.CIRCLE_PARTY_ACCOUNT_SERVICE)
            IAccountIntegrationService accountIntegrationService,
            GmsDAO gmsDAO, IUserRepository userRepository,
            IAccountRepository accountRepository,
            MessageProvider messageProvider) {
        this.accountIntegrationService = accountIntegrationService;
        this.gmsDAO = gmsDAO;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.messageProvider = messageProvider;
    }


    @Override
    public BaseResponse processVirtualAccountCreation(String userEmail) {
        // Prepare default error response.
        BaseResponse response = new BaseResponse();
        String code = ResponseCode.SYSTEM_ERROR;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));

        // Fetch the master bank account for linking.
        MasterBankAccount masterBankAccount = gmsDAO.getMasterBankAccount();
        GmsUser user = userRepository.findByEmailAddress(userEmail);
        String fullName = StringValues.EMPTY_STRING;

        if(user.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name()))
            fullName = String.join(StringValues.SINGLE_EMPTY_SPACE, user.getLastName(), user.getFirstName(), user.getMiddleName());
        else if(user.getUserType().equalsIgnoreCase(UserType.BUSINESS.name()))
            fullName = user.getBusinessName();

        log.info("Customer full name: {}", fullName);

        AccountBillingDetails billingDetails = masterBankAccount.getAccountBillingDetails();
        billingDetails.setName(fullName);
        masterBankAccount.setBillingDetailsJson(JSON.toJson(billingDetails));

        // Link the master account to Circle Payment gateway.
        BaseResponse linkingResponse = accountIntegrationService.processLinkWireAccount(masterBankAccount);

        // Check if account linking is successful, then get the wire account instructions.
        if(linkingResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            Object[] data = (Object[]) linkingResponse.getOtherDetails();
            CircleLinkWireAccountResponseDTO linkingResponseDTO = (CircleLinkWireAccountResponseDTO) data[0];
            String requestIdempotencyKey = (String) data[1];

            // Get wire instructions.
            BaseResponse wireInstructionResponse = accountIntegrationService.processCreateCustomerVirtualAccountInstruction(linkingResponseDTO.getId());
            if(wireInstructionResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
                CircleCreateWireAccountInstructionResponseDTO wireInstructionDTO = (CircleCreateWireAccountInstructionResponseDTO) wireInstructionResponse.getOtherDetails();
                VirtualAccountCache createdAccount = createVirtualAccountCache(linkingResponseDTO, wireInstructionDTO, fullName, requestIdempotencyKey, userEmail);

                code = ResponseCode.SUCCESS;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                response.setOtherDetails(createdAccount);
                response.setOtherDetailsJson(JSON.toJson(createdAccount));
            }
        }

        return response;
    }

    private VirtualAccountCache createVirtualAccountCache(CircleLinkWireAccountResponseDTO linkingResponseDTO, CircleCreateWireAccountInstructionResponseDTO wireInstructionDTO, String fullName, String requestIdempotencyKey, String userEmail){
        // Create a new virtual account entry
        VirtualAccountCache accountCache = new VirtualAccountCache();
        accountCache.setInternalId(UUID.randomUUID().toString());
        accountCache.setRequestIdempotencyId(requestIdempotencyKey);
        accountCache.setExternalId(linkingResponseDTO.getId());
        accountCache.setVendor(Vendor.CIRCLE.name());
        accountCache.setAccountNumber(wireInstructionDTO.getBeneficiaryBank().getAccountNumber());
        accountCache.setRoutingNumber(wireInstructionDTO.getBeneficiaryBank().getRoutingNumber());
        accountCache.setAccountName(wireInstructionDTO.getBeneficiaryBank().getName());
        accountCache.setCreatedAt(LocalDateTime.now().toString());
        accountCache.setUpdatedAt(LocalDateTime.now().toString());
        accountCache.setStatus(linkingResponseDTO.getStatus().toUpperCase());
        accountCache.setDescription(String.format("Wire Virtual account associated with customer: %s, with email: %s", fullName, userEmail));
        accountCache.setTrackingRef(wireInstructionDTO.getTrackingRef() == null || wireInstructionDTO.getTrackingRef().isBlank() || wireInstructionDTO.getTrackingRef().isEmpty() ? linkingResponseDTO.getTrackingRef() : wireInstructionDTO.getTrackingRef());
        accountCache.setFingerPrint(linkingResponseDTO.getFingerPrint());
        accountCache.setBillingDetailsJson(JSON.toJson(linkingResponseDTO.getBillingDetails()));
        accountCache.setBankAddressJson(JSON.toJson(linkingResponseDTO.getBankAddress()));
        accountCache.setBeneficiaryName(wireInstructionDTO.getBeneficiary().getName());
        accountCache.setBeneficiaryAddress1(wireInstructionDTO.getBeneficiary().getAddress1());
        accountCache.setBeneficiaryAddress2(wireInstructionDTO.getBeneficiary().getAddress2());
        accountCache.setVirtualAccountEnabled(wireInstructionDTO.getVirtualAccountEnabled());
        accountCache.setInternalCustomerName(fullName);
        accountCache.setInternalCustomerEmail(userEmail);
        accountCache.setLinkingLogs(JSON.toJson(linkingResponseDTO));
        accountCache.setCreationLogs(JSON.toJson(wireInstructionDTO));

        return accountRepository.saveAndFlush(accountCache);
    }

}
