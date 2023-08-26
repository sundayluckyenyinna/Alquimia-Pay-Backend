package com.gms.alquimiapay.scheduler.pooling.circle;

import com.gms.alquimiapay.constants.*;
import com.gms.alquimiapay.integration.external.circle.dto.deposit.data.CircleBankDepositResponseData;
import com.gms.alquimiapay.integration.external.circle.dto.deposit.response.CircleBankDepositResponseDTO;
import com.gms.alquimiapay.integration.internal.deposit.IIntegrationDepositService;
import com.gms.alquimiapay.modules.account.model.AccountDeposit;
import com.gms.alquimiapay.modules.account.model.VirtualAccountCache;
import com.gms.alquimiapay.modules.account.repository.IAccountDepositRepository;
import com.gms.alquimiapay.modules.account.repository.IAccountRepository;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.transaction.constant.HistoryType;
import com.gms.alquimiapay.modules.wallet.constant.WalletBalanceType;
import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import com.gms.alquimiapay.modules.wallet.repository.IGmsWalletCacheRepository;
import com.gms.alquimiapay.modules.wallet.service.IWalletService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.util.EmailMessenger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Configuration
@EnableScheduling
public class WireBankDepositWalletUpdateCronJob
{
    private final IIntegrationDepositService depositService;
    private final IAccountRepository accountRepository;
    private final EmailMessenger emailMessenger;
    private final IAccountDepositRepository depositRepository;
    private final IGmsWalletCacheRepository walletCacheRepository;
    private final IWalletService walletService;


    @Autowired
    public WireBankDepositWalletUpdateCronJob(
            @Qualifier(value = QualifierValue.CIRCLE_PARTY_DEPOSIT_SERVICE)
            IIntegrationDepositService depositService,
            IAccountRepository accountRepository,
            EmailMessenger emailMessenger,
            IAccountDepositRepository depositRepository,
            IGmsWalletCacheRepository walletCacheRepository,
            @Qualifier(QualifierService.LOCAL_WALLET_SERVICE)
            IWalletService walletService
    )
    {
        this.depositService = depositService;
        this.accountRepository = accountRepository;
        this.emailMessenger = emailMessenger;
        this.depositRepository = depositRepository;
        this.walletCacheRepository = walletCacheRepository;
        this.walletService = walletService;
    }


    @Scheduled(cron = CronExpression.EVERY_5_SECONDS)
    public void processDepositAndCustomerWalletUpdate(){
        AccountDeposit lastRecord = depositRepository.findLastRecord();
        String startDateTime = StringValues.EMPTY_STRING;
        if(lastRecord != null) {
            startDateTime = lastRecord.getCreatedAt().endsWith("Z") ? lastRecord.getCreatedAt() : lastRecord.getCreatedAt().concat("Z");
            log.info("Last Record: {}", lastRecord);
        }

        BaseResponse response = depositService.processFetchAllBankDeposit(startDateTime);
        if(response.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            CircleBankDepositResponseDTO responseDTO = (CircleBankDepositResponseDTO) response.getOtherDetails();
            List<CircleBankDepositResponseData> depositData = responseDTO.getData();

            for (CircleBankDepositResponseData data : depositData) {
                AccountDeposit deposit = depositRepository.findByDepositId(data.getId());

                if (deposit == null || deposit.getStatus().toUpperCase().equalsIgnoreCase(ModelStatus.PENDING.name())) {

                    // Save the deposit data to the database
                    if(deposit == null) {
                        deposit = new AccountDeposit();
                    }
                    deposit.setDepositId(data.getId());
                    deposit.setCurrency(data.getAmount().getCurrency());
                    deposit.setAmount(data.getAmount().getAmount());
                    deposit.setBeneficiaryWalletOrAccountId(data.getDestination().getId());
                    deposit.setBeneficiaryType(data.getDestination().getType());
                    deposit.setSourceWalletId(data.getSourceWalletId());
                    deposit.setCreatedAt(data.getCreateDate().replace("Z", StringValues.EMPTY_STRING));
                    deposit.setUpdatedAt(data.getUpdateDate().replace("Z", StringValues.EMPTY_STRING));
                    deposit.setStatus(data.getStatus().toUpperCase());
                    deposit.setSourceType("WIRE");
                    deposit.setTransactionType(HistoryType.DEPOSIT.name());
                    depositRepository.saveAndFlush(deposit);

                    VirtualAccountCache accountCache = accountRepository.findByExternalId(data.getSourceWalletId());
                    if (accountCache != null) {
                        String userEmail = accountCache.getInternalCustomerEmail();

                        deposit.setSourceCustomerEmail(userEmail);
                        deposit.setSourceCustomerName(accountCache.getInternalCustomerName());
                        deposit.setInternalRef(UUID.randomUUID().toString());
                        deposit.setBeneficiaryAccountName(accountCache.getBeneficiaryName());
                        depositRepository.saveAndFlush(deposit);

                        // Update the customer's wallet only if the status is 'COMPLETE'
                        if(data.getStatus().toUpperCase().equalsIgnoreCase(ModelStatus.COMPLETE.name())) {
                            GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(userEmail);
                            if (walletCache != null) {

                                String depositAmount = data.getAmount().getAmount();
                                walletService.processCreditWalletRequest(depositAmount, walletCache, WalletBalanceType.AVAILABLE_BALANCE);
                                walletService.processCreditWalletRequest(depositAmount, walletCache, WalletBalanceType.PENDING_BALANCE);

                                // Update deposit entry
                                deposit.setOwnerWalletBalance(walletCache.getAvailableBalance());
                                depositRepository.saveAndFlush(deposit);

                                // Send email of deposit
                                String balanceString = String.valueOf(walletCache.getAvailableBalance());
                                sendDepositSuccessEmail(userEmail, deposit, balanceString, accountCache.getInternalCustomerName());
                            }
                        }
                    }
                }

            }

        }
    }

    private void sendDepositSuccessEmail(String userEmail, AccountDeposit deposit, String newBalanceString, String fullName){
        DecimalFormat df = new DecimalFormat("###,###,###.00");
        Map<String, String> data = new HashMap<>();
        data.put("fullName", fullName);
        data.put("currency", deposit.getCurrency());
        data.put("amount", df.format(new BigDecimal(deposit.getAmount())));
        data.put("balance", df.format(new BigDecimal(newBalanceString)));

        String emailSubject = "Actualización del saldo de la billetera GMS";
        emailMessenger.sendMessageWithData(userEmail, "deposit-update", emailSubject, data);
    }
}
