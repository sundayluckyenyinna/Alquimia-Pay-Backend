package com.gms.alquimiapay.webhook.service.circle;

import com.gms.alquimiapay.constants.Creator;
import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.transaction.model.TransactionEntry;
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
import com.gms.alquimiapay.webhook.constants.CirclePaymentType;
import com.gms.alquimiapay.webhook.dto.circle.CircleWebhookTransferDTO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * This class handles the notification webhook service associated with the Circle notification webhook.
 */

@Slf4j
@Service
public class CircleTransferNotificationService implements CircleNotification
{

    private final IUserRepository userRepository;
    private final ITransactionEntryRepository transactionEntryRepository;
    private final IGmsWalletCacheRepository walletCacheRepository;
    private final IWalletService walletService;
    private final ITransactionService transactionService;
    private final EmailMessenger emailMessenger;
    private static final Gson JSON = new Gson();

    @Autowired
    public CircleTransferNotificationService(
            ITransactionService transactionService,
            IUserRepository userRepository, ITransactionEntryRepository transactionEntryRepository,
            IGmsWalletCacheRepository walletCacheRepository,
            @Qualifier(QualifierService.LOCAL_WALLET_SERVICE) IWalletService walletService,
            ITransactionService transactionService1, EmailMessenger emailMessenger
    ) {
        this.userRepository = userRepository;
        this.transactionEntryRepository = transactionEntryRepository;
        this.walletCacheRepository = walletCacheRepository;
        this.walletService = walletService;
        this.transactionService = transactionService1;
        this.emailMessenger = emailMessenger;
    }

    @Override
    public boolean doNotification(String notificationObject) {

        // Convert the notificationObject json to a transfer payment dto object.
        CircleWebhookTransferDTO transferDTO = JSON.fromJson(notificationObject, CircleWebhookTransferDTO.class);

        // Check if the transaction entry is absent.
        TransactionEntry transactionEntry = transactionEntryRepository.findByExternalRef(transferDTO.getId());
        if(transactionEntry == null){
            return false;
        }

        transactionEntry.setInternalRef(UUID.randomUUID().toString());
        transactionEntry.setExternalRef(transferDTO.getId());
        transactionEntry.setCreatedAt(transferDTO.getCreateDate().replace("Z", StringValues.EMPTY_STRING));
        transactionEntry.setCreatedBy(Creator.SYSTEM.name());
        transactionEntry.setUpdatedAt(LocalDateTime.now().toString());
        transactionEntry.setUpdatedBy(Creator.SYSTEM.name());
        transactionEntry.setTransactionType("TRANSFER_PAYMENT");
        transactionEntry.setTransactionAmount(transferDTO.getAmount().getAmount());
        transactionEntry.setExternalStatus(transferDTO.getStatus().toUpperCase());
        transactionEntry.setIsUpdatedByExternalVendor(true);
        transactionEntry.setTransactionHash(transferDTO.getTransactionHash());

        String sourceType = transferDTO.getSource().getType().toUpperCase();
        transactionEntry.setSourceType(sourceType);

        if(sourceType.equalsIgnoreCase(CirclePaymentType.WALLET.name())){
            transactionEntry.setSourceWalletId(transferDTO.getSource().getId());
        }
        else if(sourceType.equalsIgnoreCase(CirclePaymentType.CARD.name()) || sourceType.equalsIgnoreCase(CirclePaymentType.WIRE.name())){
            transactionEntry.setSourceCardId(transferDTO.getSource().getId());
        }
        else if(sourceType.equalsIgnoreCase(CirclePaymentType.BLOCKCHAIN.name())){
            transactionEntry.setSourceBlockchainAddress(transferDTO.getSource().getAddress());
            transactionEntry.setSourceBlockchain(transferDTO.getSource().getChain().toUpperCase());
        }

        transactionEntry.setCreatedAt(transferDTO.getCreateDate().replace("Z", StringValues.EMPTY_STRING));

        // Persist the created or update transaction entry to the database.
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Build data for email sending.
        GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(transactionEntry.getCustomerEmail());
        String name = transactionEntry.getCustomerName();

        Map<String, String> map = new HashMap<>();
        map.put("fullName", name);
        map.put("currency", transferDTO.getAmount().getCurrency() == null ? "USD" : transferDTO.getAmount().getCurrency());
        map.put("amount", transferDTO.getAmount().getAmount());
        map.put("balance", walletCache.getAvailableBalance());
        map.put("benName", transactionEntry.getCustomerBeneficiaryName());
        map.put("benAccount", transactionEntry.getCustomerBeneficiaryAccount() == null ? StringValues.EMPTY_STRING : transactionEntry.getCustomerBeneficiaryAccount());
        map.put("benPhone", transactionEntry.getCustomerBeneficiaryPhone());
        map.put("fee", transactionEntry.getTransactionFee());
        map.put("totalAmount", transactionEntry.getTransactionTotalAmount());

        // Check if the entry has been completed in status, then update wallet and send notification email to the end user.
        if (transferDTO.getStatus().equalsIgnoreCase(ModelStatus.COMPLETE.name())) {

            // Update the pending balance of the customer.
            String totalAmount = transactionEntry.getTransactionTotalAmount();
            WalletOperationResult debit = walletService.processDebitWalletRequest(totalAmount, walletCache, WalletBalanceType.PENDING_BALANCE);
            if(debit.getHasError()){
                transactionEntry.setFailureReason(debit.getResponseCode().concat(StringValues.SINGLE_EMPTY_SPACE).concat(debit.getResponseMessage()));
            }

            // Send success email to the customer.
            emailMessenger.sendMessageWithData(transactionEntry.getCustomerEmail(), "transaction-update", "Actualización de transacciones GMS", map);

            // Submit the customer beneficiary details of the transaction to Alquimia.
            GmsUser user = userRepository.findByEmailAddress(transactionEntry.getCustomerEmail());
            transactionService.submitRemittanceToAlquimiaForTransactionEntry(user, transactionEntry);
        }

        // Refund the customer if the status with Circle is FAILED
        if(transferDTO.getStatus().toUpperCase().equalsIgnoreCase(ModelStatus.FAILED.name())){
            transactionEntry.setFailureReason("Failed at Circle on webhook notification");
            transactionEntryRepository.saveAndFlush(transactionEntry);

            BaseResponse reversalResponse = transactionService.processCashTransferFundReversal(transactionEntry.getInternalRef());
            if(reversalResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
                log.info("Transfer reversal successful");
                transactionEntry.setInternalStatus(ModelStatus.REVERSED.name());
                transactionEntryRepository.saveAndFlush(transactionEntry);

            }else {
                log.error("Transfer reversal failed with reason: {}", reversalResponse.getResponseMessage());
                transactionEntry.setInternalStatus(ModelStatus.FAILED_REVERSED.name());
                transactionEntryRepository.saveAndFlush(transactionEntry);
            }
        }

        return true;
    }
}
