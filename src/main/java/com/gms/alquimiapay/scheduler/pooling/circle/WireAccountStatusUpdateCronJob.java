package com.gms.alquimiapay.scheduler.pooling.circle;

import com.gms.alquimiapay.constants.CronExpression;
import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.integration.internal.account.IAccountIntegrationService;
import com.gms.alquimiapay.modules.account.model.VirtualAccountCache;
import com.gms.alquimiapay.modules.account.repository.IAccountRepository;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.util.EmailMessenger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableScheduling
public class WireAccountStatusUpdateCronJob
{
    private final IAccountRepository accountRepository;
    private final IAccountIntegrationService accountIntegrationService;
    private final EmailMessenger emailMessenger;
    private final IUserRepository userRepository;


    @Autowired
    public WireAccountStatusUpdateCronJob(
            IAccountRepository accountRepository,
            IAccountIntegrationService accountIntegrationService,
            EmailMessenger emailMessenger,
            IUserRepository userRepository
    )
    {
        this.accountRepository = accountRepository;
        this.accountIntegrationService = accountIntegrationService;
        this.emailMessenger = emailMessenger;
        this.userRepository = userRepository;
    }


    @Scheduled(cron = CronExpression.EVERY_5_SECONDS)
    public void processCircleVirtualAccountStatusUpdate(){
        List<VirtualAccountCache> pendingAccounts = accountRepository.findByStatus(ModelStatus.PENDING.name());

        pendingAccounts.forEach(account -> {
            String externalId = account.getExternalId().trim();
            BaseResponse response = accountIntegrationService.processGetSingleAccountStatusRequest(externalId);
            if(response.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
                String status = (String) response.getOtherDetails();

                log.info("Account status: {}", status == null ? "null" : status.toUpperCase());

                if(status != null && !status.equalsIgnoreCase(ModelStatus.PENDING.name())){
                    account.setStatus(status.toUpperCase());
                    account.setUpdatedAt(LocalDateTime.now().toString());
                    accountRepository.saveAndFlush(account);

                    // Send email to the user email if the status is COMPLETE
                    if(status.equalsIgnoreCase(ModelStatus.COMPLETE.name())) {
                        sendWireVirtualAccountStatusUpdate(account);
                    }
                }
            }
        });
    }

    private void sendWireVirtualAccountStatusUpdate(VirtualAccountCache account){
        try{
            Map<String, String> data = new HashMap<>();
            String ownerEmail = account.getInternalCustomerEmail().trim();
            GmsUser user = userRepository.findByEmailAddress(ownerEmail);
            data.put("fullName", user.getName());

            emailMessenger.sendMessageWithData(user.getEmailAddress(), "wire-account-status-update", "Actualización del estado de la cuenta virtual de GMS Wire", data);
        }catch (Exception e){
            e.printStackTrace();
            log.info("Exception while trying to send virtual account status email: {}", e.getMessage());
        }
    }

}
