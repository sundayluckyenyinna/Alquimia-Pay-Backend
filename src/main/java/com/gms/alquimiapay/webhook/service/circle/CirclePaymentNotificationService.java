package com.gms.alquimiapay.webhook.service.circle;

import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.integration.internal.deposit.IIntegrationDepositService;
import com.gms.alquimiapay.modules.account.model.AccountDeposit;
import com.gms.alquimiapay.modules.account.model.VirtualAccountCache;
import com.gms.alquimiapay.modules.account.repository.IAccountDepositRepository;
import com.gms.alquimiapay.modules.account.repository.IAccountRepository;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.wallet.constant.WalletBalanceType;
import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import com.gms.alquimiapay.modules.wallet.repository.IGmsWalletCacheRepository;
import com.gms.alquimiapay.modules.wallet.service.IWalletService;
import com.gms.alquimiapay.util.EmailMessenger;
import com.gms.alquimiapay.webhook.constants.CirclePaymentType;
import com.gms.alquimiapay.webhook.dto.circle.CircleWebhookPaymentDTO;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * This service handles the notification webhook associated with the Circle Transfer creation and update.
 */
@Service
public class CirclePaymentNotificationService implements CircleNotification
{

    private final IAccountRepository accountRepository;
    private final EmailMessenger emailMessenger;
    private final IAccountDepositRepository depositRepository;
    private final IGmsWalletCacheRepository walletCacheRepository;
    private final IWalletService walletService;
    private static final Gson JSON = new Gson();

    @Autowired
    public CirclePaymentNotificationService(
            IIntegrationDepositService depositService,
            IAccountRepository accountRepository,
            EmailMessenger emailMessenger,
            IAccountDepositRepository depositRepository,
            IGmsWalletCacheRepository walletCacheRepository,
            @Qualifier(QualifierService.LOCAL_WALLET_SERVICE)
            IWalletService walletService)
    {
        this.accountRepository = accountRepository;
        this.emailMessenger = emailMessenger;
        this.depositRepository = depositRepository;
        this.walletCacheRepository = walletCacheRepository;
        this.walletService = walletService;
    }

    @Override
    public boolean doNotification(String notificationObject) {

        // Convert the notification string to an appropriate notification payload/dto
        CircleWebhookPaymentDTO paymentDTO =  JSON.fromJson(notificationObject, CircleWebhookPaymentDTO.class);

        // Get the type of the payment and perform necessary actions
        AccountDeposit deposit = depositRepository.findByDepositId(paymentDTO.getId());

        if (deposit == null || deposit.getStatus().toUpperCase().equalsIgnoreCase(ModelStatus.PENDING.name())) {

            // Save the deposit data to the database
            if(deposit == null) {
                deposit = new AccountDeposit();
            }
            deposit.setDepositId(paymentDTO.getId());
            deposit.setCurrency(paymentDTO.getAmount().getCurrency());
            deposit.setAmount(paymentDTO.getAmount().getAmount());
            deposit.setBeneficiaryWalletOrAccountId(paymentDTO.getMerchantWalletId());
            String sourceType = paymentDTO.getSource().getType().toUpperCase();

            deposit.setSourceType(sourceType);

            // Get the type of source of the deposit and set appropriate values.
            if(sourceType.equalsIgnoreCase(CirclePaymentType.WALLET.name())){
                deposit.setSourceWalletId(paymentDTO.getSource().getId());
            }
            else if(sourceType.equalsIgnoreCase(CirclePaymentType.CARD.name()) || sourceType.equalsIgnoreCase(CirclePaymentType.WIRE.name())){
                deposit.setSourceCardId(paymentDTO.getSource().getId());
            }
            else if(sourceType.equalsIgnoreCase(CirclePaymentType.BLOCKCHAIN.name())){
                deposit.setSourceBlockchainAddress(paymentDTO.getSource().getAddress());
                deposit.setSourceBlockchain(paymentDTO.getSource().getChain().toUpperCase());
            }

            deposit.setBeneficiaryType(CirclePaymentType.WALLET.name());
            deposit.setSourceWalletId(paymentDTO.getSource().getId());
            deposit.setCreatedAt(paymentDTO.getCreateDate().replace("Z", StringValues.EMPTY_STRING));
            deposit.setUpdatedAt(paymentDTO.getUpdateDate().replace("Z", StringValues.EMPTY_STRING));
            deposit.setStatus(paymentDTO.getStatus().toUpperCase());
            depositRepository.saveAndFlush(deposit);

            VirtualAccountCache accountCache = accountRepository.findByExternalId(paymentDTO.getSource().getId());
            if (accountCache != null) {
                String userEmail = accountCache.getInternalCustomerEmail();

                deposit.setSourceCustomerEmail(userEmail);
                deposit.setSourceCustomerName(accountCache.getInternalCustomerName());
                deposit.setInternalRef(UUID.randomUUID().toString());
                deposit.setBeneficiaryAccountName(accountCache.getBeneficiaryName());
                depositRepository.saveAndFlush(deposit);

                // Update the customer's wallet only if the status is 'COMPLETE'
                if(paymentDTO.getStatus().toUpperCase().equalsIgnoreCase(ModelStatus.COMPLETE.name())) {
                    GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(userEmail);
                    if (walletCache != null) {

                        String depositAmount = paymentDTO.getAmount().getAmount();
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

        return true;
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
