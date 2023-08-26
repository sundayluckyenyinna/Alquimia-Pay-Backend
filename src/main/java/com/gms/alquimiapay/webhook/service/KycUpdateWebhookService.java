package com.gms.alquimiapay.webhook.service;

import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.integration.external.sila.model.SilaUser;
import com.gms.alquimiapay.integration.external.sila.repository.SilaUserRepository;
import com.gms.alquimiapay.integration.internal.identity.IIntegrationIdentityService;
import com.gms.alquimiapay.modules.kyc.constant.KycStatus;
import com.gms.alquimiapay.modules.kyc.model.UserKycVerification;
import com.gms.alquimiapay.modules.kyc.repository.UserKycVerificationRepository;
import com.gms.alquimiapay.modules.user.constants.UserType;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.util.EmailMessenger;
import com.gms.alquimiapay.webhook.dto.KycUpdateEventDTO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class KycUpdateWebhookService
{
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private SilaUserRepository silaUserRepository;
    @Autowired
    private IIntegrationIdentityService integrationIdentityService;
    @Autowired
    private UserKycVerificationRepository userKycVerificationRepository;
    @Autowired
    private EmailMessenger emailMessenger;

    private static final Gson JSON = new Gson();

    public BaseResponse processKycUpdate(KycUpdateEventDTO eventDTO){
        // Get the user for which this event was triggered.
        String userHandle = eventDTO.getEventDetails().getEntity();
        SilaUser silaUser = silaUserRepository.findBySilaUserHandle(userHandle);

        GmsUser user = userRepository.findByEmailAddress(silaUser.getGmsUserEmail());

        try {
            log.info("KYC Event details: {}", JSON.toJson(eventDTO));
        }catch (Exception ignored){}

        // Get the Kyc entity and update it in the database.
        UserKycVerification kycVerification = userKycVerificationRepository.findByUserEmail(user.getEmailAddress());
        if(kycVerification != null){
            log.info("Found user KYC details: {}", JSON.toJson(kycVerification));
            String kycOutcome = eventDTO.getEventDetails().getOutcome().toUpperCase();
            kycVerification.setStatus(kycOutcome);
            kycVerification.setUpdatedAt(LocalDateTime.now().toString());
            kycVerification.setExternalReference(eventDTO.getEventUUID());
            kycVerification.setUpdatedAt(eventDTO.getEventTime());
            kycVerification.setLogs(JSON.toJson(eventDTO));
            userKycVerificationRepository.saveAndFlush(kycVerification);

            // Check for the kyc and fetch the details.
            BaseResponse baseResponse = integrationIdentityService.processKycRequestChecking(user);
            if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
                kycVerification.setLogs(baseResponse.getOtherDetailsJson());
                kycVerification.setKycTier("2");
                userKycVerificationRepository.saveAndFlush(kycVerification);

                String recipientMail;
                String subject = "GMS KYC Status Update";

                Map<String, String> data = new HashMap<>();
                if(user.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name())){
                    data.put("lastName", user.getLastName());
                    data.put("firstName", user.getFirstName());
                    recipientMail = user.getEmailAddress();
                }
                else{
                    data.put("lastName", user.getBusinessName());
                    data.put("firstName", StringValues.EMPTY_STRING);
                    recipientMail = user.getBusinessEmail();
                }

                // Send email to the user that their kyc has been activated.
                if(kycOutcome.equalsIgnoreCase(KycStatus.PASSED.name())) {
                    CompletableFuture.runAsync(() -> {
                        emailMessenger.sendMessageWithData(recipientMail, "kyc-notify-success", subject, data);
                    });
                }
                else if (kycOutcome.equalsIgnoreCase(KycStatus.PENDING.name())){
                    CompletableFuture.runAsync(() -> {
                        emailMessenger.sendMessageWithData(recipientMail, "kyc-notify-pending", subject, data);
                    });
                }
                else if(kycOutcome.equalsIgnoreCase(KycStatus.FAILED.name())){
                    CompletableFuture.runAsync(() -> {
                        emailMessenger.sendMessageWithData(recipientMail, "kyc-notify-failure", subject, data);
                    });
                }

            }
        }

        log.info("Error while handling webhook url for checking KYC request");
        return new BaseResponse();
    }
}
