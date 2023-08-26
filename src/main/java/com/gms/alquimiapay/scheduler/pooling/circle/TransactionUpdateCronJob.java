package com.gms.alquimiapay.scheduler.pooling.circle;

import com.gms.alquimiapay.constants.CronExpression;
import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.transaction.model.TransactionEntry;
import com.gms.alquimiapay.modules.transaction.payload.data.TransactionStatusResponseData;
import com.gms.alquimiapay.modules.transaction.payload.response.TransactionStatusResponsePayload;
import com.gms.alquimiapay.modules.transaction.repository.ITransactionEntryRepository;
import com.gms.alquimiapay.modules.transaction.service.ITransactionService;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.modules.wallet.constant.WalletBalanceType;
import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletOperationResult;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This cron job is scheduled to run every day at midnight in order to ensure the reconciliation of end-users transfers
 * that were not pushed by the Circle Notification system. It is here to ensure that the end-user transactions is reconciled.
 */

@Slf4j
@Configuration
@EnableScheduling
public class TransactionUpdateCronJob
{
    private final ITransactionService transactionService;
    private final ITransactionEntryRepository transactionEntryRepository;
    private final IGmsWalletCacheRepository walletCacheRepository;
    private final IWalletService walletService;
    private final EmailMessenger emailMessenger;
    private final IUserRepository userRepository;

    @Autowired
    public TransactionUpdateCronJob(
            ITransactionService transactionService,
            ITransactionEntryRepository transactionEntryRepository,
            IGmsWalletCacheRepository walletCacheRepository,
            @Qualifier(QualifierService.LOCAL_WALLET_SERVICE) IWalletService walletService,
            EmailMessenger emailMessenger,
            IUserRepository userRepository) {
        this.transactionService = transactionService;
        this.transactionEntryRepository = transactionEntryRepository;
        this.walletCacheRepository = walletCacheRepository;
        this.walletService = walletService;
        this.emailMessenger = emailMessenger;
        this.userRepository = userRepository;
    }


    @Scheduled(cron = CronExpression.EVERY_10_SECONDS)
    public void processTransactionStatusUpdate(){

        // Process transaction service for each record that are still pending and wasn't updated by Circle webhook.
        List<TransactionEntry> transactionEntries = transactionEntryRepository.findByExternalStatus(ModelStatus.PENDING.name());
        log.info("Transaction size to process: {}", transactionEntries.size());
        
        for(TransactionEntry entry : transactionEntries){

            try{
                String transactionId = entry.getExternalRef();

               if(transactionId != null) {
                   log.info("Valid Transaction entry ID to process: {}", transactionId);

                   TransactionStatusResponsePayload responsePayload = transactionService.processTransactionStatus(transactionId);
                   TransactionStatusResponseData data = responsePayload.getResponseData();

                   if (responsePayload.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)) {
                       if (!data.getStatus().equalsIgnoreCase(ModelStatus.PENDING.name())) {
                           entry.setUpdatedAt(LocalDateTime.now().toString());
                           entry.setTransactionHash(data.getTransactionHash());
                           entry.setExternalStatus(data.getStatus().toUpperCase());
                           entry.setIsUpdatedByExternalVendor(true);
                           transactionEntryRepository.saveAndFlush(entry);


                           // Build data for email.
                           GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(entry.getCustomerEmail());
                           String name = entry.getCustomerName();

                           Map<String, String> map = new HashMap<>();
                           map.put("fullName", name);
                           map.put("currency", entry.getCurrency() == null ? "USD" : entry.getCurrency());
                           map.put("amount", entry.getTransactionAmount());
                           map.put("balance", walletCache.getAvailableBalance());
                           map.put("benName", entry.getCustomerBeneficiaryName());
                           map.put("benAccount", entry.getCustomerBeneficiaryAccount() == null ? StringValues.EMPTY_STRING : entry.getCustomerBeneficiaryAccount());
                           map.put("benPhone", entry.getCustomerBeneficiaryPhone());
                           map.put("fee", entry.getTransactionFee());
                           map.put("totalAmount", entry.getTransactionTotalAmount());

                           // Debit the customer if the transfer is successfully resolved with Circle.
                           if (data.getStatus().equalsIgnoreCase(ModelStatus.COMPLETE.name())) {

                               // Update the pending balance of the customer.
                               String totalAmount = entry.getTransactionTotalAmount();
                               WalletOperationResult debit = walletService.processDebitWalletRequest(totalAmount, walletCache, WalletBalanceType.PENDING_BALANCE);
                                if(debit.getHasError()){
                                    entry.setFailureReason(debit.getResponseCode().concat(StringValues.SINGLE_EMPTY_SPACE).concat(debit.getResponseMessage()));
                                }

                               // Send success email to the customer.
                               emailMessenger.sendMessageWithData(entry.getCustomerEmail(), "transaction-update", "Actualización de transacciones GMS", map);

                               // Submit the customer beneficiary details of the transaction to Alquimia.
                               GmsUser user = userRepository.findByEmailAddress(entry.getCustomerEmail());
                               transactionService.submitRemittanceToAlquimiaForTransactionEntry(user, entry);
                           }

                           // Refund the customer if the transfer failed at Circle.
                           if(data.getStatus().equalsIgnoreCase(ModelStatus.FAILED.name())){
                               entry.setFailureReason(data.getFailureReason());
                               transactionEntryRepository.saveAndFlush(entry);

                               BaseResponse reversalResponse = transactionService.processCashTransferFundReversal(entry.getInternalRef());
                               if(reversalResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
                                   entry.setInternalStatus(ModelStatus.REVERSED.name());
                                   transactionEntryRepository.saveAndFlush(entry);

                               }else {
                                   entry.setInternalStatus(ModelStatus.FAILED_REVERSED.name());
                                   transactionEntryRepository.saveAndFlush(entry);
                               }
                           }
                       }
                   }
               }

            }catch (Exception exception){
                exception.printStackTrace();
                log.error("Error while running transaction query: {}", exception.getMessage());
            }

        }
    }
}
