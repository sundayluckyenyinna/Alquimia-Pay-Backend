package com.gms.alquimiapay.modules.transaction.service;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.*;
import com.gms.alquimiapay.dao.GmsDAO;
import com.gms.alquimiapay.integration.external.alquimia.dto.data.AlquimiaUser;
import com.gms.alquimiapay.integration.external.alquimia.dto.data.Beneficiary;
import com.gms.alquimiapay.integration.external.alquimia.dto.data.Remittance;
import com.gms.alquimiapay.integration.external.alquimia.dto.data.Sender;
import com.gms.alquimiapay.integration.external.circle.dto.transaction.response.TransferResponseDTO;
import com.gms.alquimiapay.integration.internal.remittance.IIntegrationRemittanceService;
import com.gms.alquimiapay.integration.internal.transaction.ITransactionIntegrationService;
import com.gms.alquimiapay.model.GmsParam;
import com.gms.alquimiapay.model.MasterBankAccount;
import com.gms.alquimiapay.model.MasterWallet;
import com.gms.alquimiapay.modules.account.model.AccountDeposit;
import com.gms.alquimiapay.modules.account.repository.IAccountDepositRepository;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.kyc.constant.Vendor;
import com.gms.alquimiapay.modules.transaction.constant.HistoryType;
import com.gms.alquimiapay.modules.transaction.model.CustomerBeneficiary;
import com.gms.alquimiapay.modules.transaction.model.GmsExchangeRate;
import com.gms.alquimiapay.modules.transaction.model.GmsRemittance;
import com.gms.alquimiapay.modules.transaction.model.TransactionEntry;
import com.gms.alquimiapay.modules.transaction.payload.data.*;
import com.gms.alquimiapay.modules.transaction.payload.pojo.TransactionPojo;
import com.gms.alquimiapay.modules.transaction.payload.request.CashTransferRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.request.CreateBeneficiaryRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.request.TransactionFeeRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.request.TransactionHistoryRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.response.*;
import com.gms.alquimiapay.modules.transaction.repository.GmsExchangeRateRepository;
import com.gms.alquimiapay.modules.transaction.repository.ICustomerBeneficiaryRepository;
import com.gms.alquimiapay.modules.transaction.repository.IGmsRemittanceRepository;
import com.gms.alquimiapay.modules.transaction.repository.ITransactionEntryRepository;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.modules.wallet.constant.WalletBalanceType;
import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletOperationResult;
import com.gms.alquimiapay.modules.wallet.repository.IGmsWalletCacheRepository;
import com.gms.alquimiapay.modules.wallet.service.IWalletService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.util.BigDecimalUtil;
import com.gms.alquimiapay.util.EmailMessenger;
import com.gms.alquimiapay.util.JwtUtil;
import com.gms.alquimiapay.util.PasswordUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionService implements ITransactionService {
    private final Environment env;
    private final GmsDAO gmsDAO;
    private final MessageProvider messageProvider;
    private final ITransactionIntegrationService transactionIntegrationService;
    private final ITransactionEntryRepository transactionEntryRepository;
    private final IGmsWalletCacheRepository walletCacheRepository;
    private final IUserRepository userRepository;
    private final IAccountDepositRepository accountDepositRepository;
    private final ICustomerBeneficiaryRepository beneficiaryRepository;
    private final IWalletService walletService;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final IIntegrationRemittanceService integrationRemittanceService;
    private final IGmsRemittanceRepository remittanceRepository;
    private final EmailMessenger emailMessenger;
    private final GmsExchangeRateRepository exchangeRateRepository;

    @Value("${third-party.alquimia.account.account-number}")
    private String poolAccountNumberForAlquimia;

    private static final Gson JSON = new Gson();

    @Autowired
    public TransactionService(
            Environment env,
            GmsDAO gmsDAO,
            MessageProvider messageProvider,
            ITransactionIntegrationService transactionIntegrationService,
            ITransactionEntryRepository transactionEntryRepository,
            IGmsWalletCacheRepository walletCacheRepository,
            IUserRepository userRepository,
            IAccountDepositRepository accountDepositRepository,
            ICustomerBeneficiaryRepository beneficiaryRepository,
            @Qualifier(QualifierService.LOCAL_WALLET_SERVICE)
                    IWalletService walletService,
            JwtUtil jwtUtil,
            PasswordUtil passwordUtil, IIntegrationRemittanceService integrationRemittanceService,
            IGmsRemittanceRepository remittanceRepository,
            EmailMessenger emailMessenger,
            GmsExchangeRateRepository exchangeRateRepository) {
        this.env = env;
        this.gmsDAO = gmsDAO;
        this.messageProvider = messageProvider;
        this.transactionIntegrationService = transactionIntegrationService;
        this.transactionEntryRepository = transactionEntryRepository;
        this.walletCacheRepository = walletCacheRepository;
        this.userRepository = userRepository;
        this.accountDepositRepository = accountDepositRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.walletService = walletService;
        this.jwtUtil = jwtUtil;
        this.passwordUtil = passwordUtil;
        this.integrationRemittanceService = integrationRemittanceService;
        this.remittanceRepository = remittanceRepository;
        this.emailMessenger = emailMessenger;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public TransactionFeeResponsePayload processTransactionFeeRequest(TransactionFeeRequestPayload requestPayload) {
        TransactionFeeResponsePayload responsePayload = new TransactionFeeResponsePayload();

        String amount = requestPayload.getAmount();
        String transactionType = requestPayload.getTransactionType();

        String paramFee = this.getInternalTransactionFee();
        BaseResponse baseResponse = transactionIntegrationService.processTransactionFeeRequest(amount, transactionType);
        String thirdPartyFee = (String) baseResponse.getOtherDetails();

        double internalFee = ((Double.parseDouble(paramFee) / 100) * Double.parseDouble(amount)) + 3.99;

        BigDecimal totalFee = new BigDecimal(thirdPartyFee)
                .add(new BigDecimal(String.valueOf(internalFee)))
                .setScale(2, RoundingMode.HALF_UP);

        String receivingCurrency = requestPayload.getReceivingCurrency();
        Double exchangeRate = getExchangeRate(CurrencyCode.USD.name(), requestPayload.getReceivingCurrency().toUpperCase());
        Double destinationAmount = BigDecimalUtil.from(requestPayload.getAmount()).doubleValue() * exchangeRate;

        // Save the transaction fee reference in the transaction logs.
        TransactionEntry transactionEntry = new TransactionEntry();
        transactionEntry.setFeeReference(String.valueOf(System.currentTimeMillis()).concat(UUID.randomUUID().toString()));
        transactionEntry.setIsFeeUsed(false);
        transactionEntry.setTransactionFee(totalFee.toString());
        transactionEntry.setAmountForFeeRequest(requestPayload.getAmount());
        transactionEntry.setTransactionType(requestPayload.getTransactionType());
        transactionEntry.setExchangeRate(exchangeRate);
        transactionEntry.setDestinationCurrency(receivingCurrency);
        transactionEntry.setDestinationAmount(BigDecimalUtil.from(destinationAmount).toString());
        transactionEntryRepository.saveAndFlush(transactionEntry);

        TransactionFeeResponseData responseData = new TransactionFeeResponseData();
        responseData.setTransactionType(requestPayload.getTransactionType());
        responseData.setIntegrationFee(thirdPartyFee);
        responseData.setInternalFee(String.valueOf(internalFee));
        responseData.setAmount(requestPayload.getAmount());
        responseData.setTotalFee(totalFee.toString());
        responseData.setReference(transactionEntry.getFeeReference());
        responseData.setReceivingCurrency(transactionEntry.getDestinationCurrency());
        responseData.setReceivingAmount(transactionEntry.getDestinationAmount());
        responseData.setExchangeRate(exchangeRate);


        String code = ResponseCode.SUCCESS;
        responsePayload.setResponseCode(code);
        responsePayload.setResponseMessage(messageProvider.getMessage(code));
        responsePayload.setResponseData(responseData);
        return responsePayload;
    }

    @Override
    public CashTransferResponsePayload processCashTransferRequest(CashTransferRequestPayload requestPayload, String token) {
        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(token));
        GmsUser user = userRepository.findByEmailAddress(email);

        log.info("User email found: {}", email);

        CashTransferResponsePayload responsePayload = new CashTransferResponsePayload();
        String code;

        // Check if the user exist
        if(Optional.ofNullable(user).isEmpty()){
            code = ResponseCode.RECORD_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        // Check if there is a fee associated with the transaction request.
        TransactionEntry transactionEntry = transactionEntryRepository.findByFeeReference(requestPayload.getFeeReference());
        if(Optional.ofNullable(transactionEntry).isEmpty()){
            code = ResponseCode.NO_TRANSACTION_ENTRY_FOR_FEE;
            responsePayload.setResponseCode(messageProvider.getMessage(code).concat(StringValues.SINGLE_EMPTY_SPACE).concat(requestPayload.getFeeReference()));
            return responsePayload;
        }

        // Check if the fee has been used
        String externalRef = transactionEntry.getExternalRef();
        if(transactionEntry.getIsFeeUsed() || externalRef != null){
            code = ResponseCode.FEE_ALREADY_USED;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));

            updateTransactionEntryOnError(transactionEntry, code);
            return responsePayload;
        }

        // Check if the amount for fee request is same as the amount used for the transaction amount.
        if(!transactionEntry.getAmountForFeeRequest().equalsIgnoreCase(requestPayload.getAmount())){
            code = ResponseCode.AMOUNT_MISMATCH;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));

            updateTransactionEntryOnError(transactionEntry, code);
            return responsePayload;
        }

        // Check if the user wallet is active.
        GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(email);
        if(!walletCache.getStatus().equalsIgnoreCase(ModelStatus.ACTIVE.name())){
            code = ResponseCode.WALLET_INACTIVE;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));

            updateTransactionEntryOnError(transactionEntry, code);
            return responsePayload;
        }

        BigDecimal totalAmount = new BigDecimal(requestPayload.getAmount())
                .add(new BigDecimal(transactionEntry.getTransactionFee()))
                .setScale(2, RoundingMode.CEILING);

        // Check if the transaction pin is correct
        if(!passwordUtil.isPasswordMatch(requestPayload.getPin(), user.getTransactionPin())){
            code = ResponseCode.INCORRECT_TRANSACTION_PIN;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));

            updateTransactionEntryOnError(transactionEntry, code);
            return responsePayload;
        }

        // Save transaction entry
        String customerName = user.getName();
        transactionEntry.setInternalRef(UUID.randomUUID().toString());
        transactionEntry.setCreatedAt(LocalDateTime.now().toString());
        transactionEntry.setCreatedBy(Creator.SYSTEM.name());
        transactionEntry.setUpdatedAt(LocalDateTime.now().toString());
        transactionEntry.setUpdatedBy(Creator.SYSTEM.name());
        transactionEntry.setTransactionAmount(requestPayload.getAmount());
        transactionEntry.setCustomerName(customerName);
        transactionEntry.setCustomerEmail(email);
        transactionEntry.setCustomerBeneficiaryName(requestPayload.getBeneficiaryFullName());
        transactionEntry.setCustomerBeneficiaryPhone(requestPayload.getBeneficiaryPhone());
        transactionEntry.setCustomerBeneficiaryAccount(poolAccountNumberForAlquimia);
        transactionEntry.setTransactionTotalAmount(totalAmount.toString());
        transactionEntry.setInternalStatus(ModelStatus.PENDING.name());
        transactionEntry.setExternalStatus(ModelStatus.PENDING.name());
        transactionEntry.setIsUpdatedByExternalVendor(false);
        transactionEntry.setVendor(gmsDAO.getActiveVendor());
        transactionEntry.setIsFeeUsed(true);
        transactionEntry.setCurrency(requestPayload.getCurrency() == null ? CurrencyCode.USD.name() : requestPayload.getCurrency());
        TransactionEntry savedEntry = transactionEntryRepository.saveAndFlush(transactionEntry);

        // Debit the customer wallet.
        WalletOperationResult debit  = walletService.processDebitWalletRequest(totalAmount.toString(), walletCache, WalletBalanceType.AVAILABLE_BALANCE);
        log.info("Wallet debit response Payload: {}", JSON.toJson(debit));

        if(debit.getHasError()){
            responsePayload.setResponseCode(debit.getResponseCode());
            responsePayload.setResponseMessage(debit.getResponseMessage());

            updateTransactionEntryOnError(transactionEntry, debit.getResponseCode());
            return responsePayload;
        }

        // Call the active Vendor for cash transfer.
        TransactionPojo transactionPojo = this.getTransactionPojoForVendor(requestPayload.getAmount());
        transactionPojo.setCustomerName(customerName);
        transactionPojo.setCustomerEmail(user.getEmailAddress());
        BaseResponse response;
        try{
            response = transactionIntegrationService.processCashToBlockchainTransactionRequest(transactionPojo);
            TransferResponseDTO responseDTO = (TransferResponseDTO) response.getOtherDetails();
            if(response.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS) && responseDTO.getErrorCode() == null){

                String description = String.format("Cash transfer from user: %s to beneficiary: %s", email, requestPayload.getBeneficiaryFullName());
                transactionEntry.setInternalStatus(ModelStatus.COMPLETE.name());
                transactionEntry.setSourceType(responseDTO.getSource().getType());
                transactionEntry.setSourceWalletId(responseDTO.getSource().getId());
                transactionEntry.setExternalStatus(responseDTO.getStatus().toUpperCase());
                transactionEntry.setExternalRef(responseDTO.getId());
                transactionEntry.setDestinationType(responseDTO.getDestination().getType());
                transactionEntry.setDestinationChain(responseDTO.getDestination().getChain());
                transactionEntry.setDestinationBlockChainAddress(responseDTO.getDestination().getAddress());
                transactionEntry.setTransactionHash(responseDTO.getTransactionHash());
                transactionEntry.setUpdatedAt(responseDTO.getCreateDate().replace("Z", StringValues.EMPTY_STRING));
                transactionEntry.setOwnerWalletBalance(walletCache.getAvailableBalance());
                transactionEntry.setDescription(description);
                transactionEntry.setFailureReason(StringValues.EMPTY_STRING);
                transactionEntryRepository.saveAndFlush(transactionEntry);

                code = ResponseCode.SUCCESS;

                CashTransferResponseData data = new CashTransferResponseData();
                data.setAmount(requestPayload.getAmount());
                data.setInternalReference(transactionEntry.getInternalRef());
                data.setExternalReference(transactionEntry.getExternalRef());
                data.setFeeReference(transactionEntry.getFeeReference());
                data.setTotalFee(transactionEntry.getTransactionFee());
                data.setTransactionType(transactionEntry.getTransactionType());
                data.setDescription(description);
                data.setInternalStatus(transactionEntry.getInternalStatus());
                data.setExternalStatus(transactionEntry.getExternalStatus());
                data.setCreatedAt(transactionEntry.getCreatedAt());
                data.setReceivingAmount(transactionEntry.getDestinationAmount());

                // Set the beneficiary details.
                BeneficiaryDetails beneficiaryDetails = new BeneficiaryDetails();
                beneficiaryDetails.setName(transactionEntry.getCustomerBeneficiaryName());
                beneficiaryDetails.setAccount(transactionEntry.getCustomerBeneficiaryAccount());
                beneficiaryDetails.setPhone(transactionEntry.getCustomerBeneficiaryPhone());
                data.setBeneficiaryDetails(beneficiaryDetails);

                responsePayload.setResponseCode(code);
                responsePayload.setResponseMessage(messageProvider.getMessage(code));
                responsePayload.setResponseData(data);
            }else{
                transactionEntry.setFailureReason(responseDTO != null ? responseDTO.getErrorCode() : StringValues.EMPTY_STRING);
                transactionEntry.setUpdatedAt(LocalDateTime.now().toString());
                transactionEntryRepository.saveAndFlush(transactionEntry);

                responsePayload.setResponseCode(response.getResponseCode());
                responsePayload.setResponseMessage(response.getResponseMessage());
            }

            return responsePayload;
        }catch (Exception e){
            transactionEntry.setFailureReason(String.format("Internal server error: %s", e.getMessage()));
            transactionEntry.setUpdatedAt(LocalDateTime.now().toString());
            transactionEntryRepository.saveAndFlush(transactionEntry);

            responsePayload.setResponseCode(ResponseCode.SYSTEM_ERROR);
            responsePayload.setResponseMessage(messageProvider.getMessage(ResponseCode.SYSTEM_ERROR));
            return responsePayload;
        }
    }

    @Override
    public void submitRemittanceToAlquimiaForTransactionEntry(GmsUser user, TransactionEntry entry){
        BaseResponse baseResponse = integrationRemittanceService.processRemittanceSubmission(user, entry);
        Object[] details = (Object[]) baseResponse.getOtherDetails();
        AlquimiaUser alquimiaUser = (AlquimiaUser) details[0];
        Remittance remittance = (Remittance) details[1];
        Sender sender = (Sender) details[2];
        Beneficiary beneficiary = (Beneficiary) details[3];

        GmsRemittance gmsRemittance = GmsRemittance.builder()
                .internalRef(String.valueOf(System.currentTimeMillis()).concat(StringValues.UNDER_SCORE).concat(baseResponse.getOtherDetailsJson()))
                .internalTransactionRef(entry.getInternalRef())
                .externalTransactionRef(entry.getExternalRef())
                .noRemittance(remittance.getNoRemittance())
                .sendAmount(remittance.getSendAmount())
                .sendCurrency(entry.getCurrency())
                .exchangeRate(entry.getExchangeRate())
                .paidAmount(remittance.getPaidAmount())
                .paidCurrency(entry.getDestinationCurrency())
                .sendType(remittance.getSendType())
                .accountType(remittance.getAccountType())
                .accountNumber(remittance.getAccountNumber())
                .senderFirstName(sender.getFirstName())
                .senderSecondName(sender.getSecondName())
                .senderLastName(sender.getLastName())
                .senderSecondLastName(sender.getSecondLastName())
                .senderHomePhone(sender.getHomePhone())
                .senderWorkPhone(sender.getWorkPhone())
                .senderAddress(sender.getAddress())
                .senderGender(sender.getGender())
                .senderBirthday(sender.getBirthday())
                .beneficiaryFirstName(beneficiary.getFirstName())
                .beneficiarySecondName(beneficiary.getSecondName())
                .beneficiaryLastName(beneficiary.getLastName())
                .beneficiarySecondLastName(beneficiary.getSecondLastName())
                .beneficiaryDepartment(beneficiary.getDepartment())
                .beneficiaryOccupation(beneficiary.getOccupation())
                .beneficiaryAddress(beneficiary.getAddress())
                .userLoginJson(JSON.toJson(alquimiaUser))
                .remittanceJson(JSON.toJson(remittance))
                .senderJson(JSON.toJson(sender))
                .beneficiaryJson(JSON.toJson(beneficiary))
                .createdAt(LocalDateTime.now().toString())
                .updatedAt(LocalDateTime.now().toString())
                .build();

        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            gmsRemittance.setSubmissionStatus(ModelStatus.COMPLETE.name());
            gmsRemittance.setFailureReason(StringValues.EMPTY_STRING);
        }else{
            gmsRemittance.setSubmissionStatus(ModelStatus.FAILED.name());
            gmsRemittance.setFailureReason(baseResponse.getOtherDetailsJson());
        }

        remittanceRepository.saveAndFlush(gmsRemittance);

        // Send successful remittance email to the customer
        Map<String, String> data = new HashMap<>();
        data.put("fullname", user.getName());
        data.put("benName", entry.getCustomerBeneficiaryName());
        data.put("amount", entry.getAmountForFeeRequest());

        emailMessenger.sendMessageWithData(user.getEmailAddress(), "remittance-submission-update", "Actualización de remesas GMS", data);
    }


    private void updateTransactionEntryOnError(TransactionEntry transactionEntry, String code){
        transactionEntry.setUpdatedAt(LocalDateTime.now().toString());
        transactionEntry.setUpdatedBy(Creator.SYSTEM.name());
        transactionEntry.setFailureReason(messageProvider.getMessage(code));
        transactionEntryRepository.saveAndFlush(transactionEntry);
    }


    @Override
    public TransactionHistoryResponsePayload processTransactionHistoryRequest(TransactionHistoryRequestPayload requestPayload, String authToken){
        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));

        List<TransactionHistoryResponseData> result;
        if(requestPayload.getHistoryType().equalsIgnoreCase(HistoryType.WITHDRAWAL.name())){
            result  = this.getOutflowTransactionHistory(requestPayload, email);
        }
        else if (requestPayload.getHistoryType().equalsIgnoreCase(HistoryType.DEPOSIT.name())){
            result = this.getInflowTransactionHistory(requestPayload, email);
        }
        else if(requestPayload.getHistoryType().equalsIgnoreCase(HistoryType.ALL.name())){
            result = new ArrayList<>();
            result.addAll(this.getOutflowTransactionHistory(requestPayload, email));
            result.addAll(this.getInflowTransactionHistory(requestPayload, email));
        }
        else {
            result = new ArrayList<>();
        }

        long limit = 50L;
        if(requestPayload.getLimit() != null && requestPayload.getLimit() != 0L)
            limit = requestPayload.getLimit();

        result.sort(Comparator.comparing(a -> LocalDateTime.parse(a.getCreatedAt())));
        Collections.reverse(result);

        TransactionHistoryResponsePayload responsePayload = new TransactionHistoryResponsePayload();
        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setResponseMessage(messageProvider.getMessage(ResponseCode.SUCCESS));
        responsePayload.setResponseData(result.stream().limit(limit).collect(Collectors.toList()));
        return responsePayload;
    }


    @Override
    public TransactionStatusResponsePayload processTransactionStatus(String transactionId){
         TransactionStatusResponsePayload responsePayload = new TransactionStatusResponsePayload();
         String code;

         BaseResponse response = transactionIntegrationService.processCashToBlockchainTransactionStatus(transactionId);
         TransferResponseDTO responseDTO = (TransferResponseDTO) response.getOtherDetails();

         TransactionStatusResponseData data = new TransactionStatusResponseData();
         data.setCreateDate(responseDTO.getCreateDate().replace("Z", StringValues.EMPTY_STRING));
         data.setTransactionHash(responseDTO.getTransactionHash());
         data.setDestinationAddress(responseDTO.getDestination().getAddress());
         data.setDestinationChain(responseDTO.getDestination().getChain());
         data.setStatus(responseDTO.getStatus().toUpperCase());
         data.setDestinationType(responseDTO.getDestination().getType());
         data.setExternalRef(responseDTO.getId());
         data.setSourceWalletType(responseDTO.getSource().getType());
         data.setCurrency(responseDTO.getAmount().getCurrency());
         data.setAmount(responseDTO.getAmount().getAmount());
         data.setSourceWalletId(responseDTO.getSource().getId());
         data.setFailureReason(responseDTO.getErrorCode());

         code = ResponseCode.SUCCESS;
         responsePayload.setResponseCode(code);
         responsePayload.setResponseMessage(messageProvider.getMessage(code));
         responsePayload.setResponseData(data);
         return responsePayload;
    }


    @Override
    public BaseResponse processCashTransferFundReversal(String internalRef){
        BaseResponse response = new BaseResponse();
        String code;

        // Check if the transaction entry exists.
        TransactionEntry entry = transactionEntryRepository.findByInternalRef(internalRef);
        if(entry == null){
            code = ResponseCode.ENTITY_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;
        }

        // Check if the transaction entry external status is already complete
        if(entry.getExternalStatus().equalsIgnoreCase(ModelStatus.COMPLETE.name())){
            code = ResponseCode.TRANSACTION_ALREADY_SETTLED;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;
        }

        // Check if the customer have a wallet.
        String totalAmountDebited = entry.getTransactionAmount();
        GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(entry.getCustomerEmail());
        if(walletCache == null){
            code = ResponseCode.ENTITY_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code).concat(" :Reason: No wallet found"));
            return response;
        }

        // Refund the customer's account.
        WalletOperationResult creditResult = walletService.processCreditWalletRequest(totalAmountDebited, walletCache, WalletBalanceType.AVAILABLE_BALANCE);
        if(creditResult.getHasError()){
            code = creditResult.getResponseCode();
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;
        }

        Map<String, String> map = new HashMap<>();
        map.put("fullName", entry.getCustomerName());
        map.put("currency", entry.getCurrency() == null ? "USD" : entry.getCurrency());
        map.put("amount", entry.getTransactionAmount());
        map.put("balance", walletCache.getAvailableBalance());
        map.put("benName", entry.getCustomerBeneficiaryName());
        map.put("benPhone", entry.getCustomerBeneficiaryPhone());
        map.put("fee", entry.getTransactionFee());
        map.put("totalAmount", entry.getTransactionTotalAmount());

        // Send email to customer on reversal
        emailMessenger.sendMessageWithData(entry.getCustomerEmail(), "transfer-reversal", "Actualización de transacciones GMS", map);

        // Here the credit refund is a success.
        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        return response;
    }


    @Override
    public CreateBeneficiaryResponsePayload processCustomerBeneficiaryCreation(CreateBeneficiaryRequestPayload requestPayload, String token) {
        CreateBeneficiaryResponsePayload responsePayload = new CreateBeneficiaryResponsePayload();
        String code;

        // Check if the customer exist in the system.
        String customerEmail = jwtUtil.getUserEmailFromJWTToken(cleanToken(token));
        GmsUser customer = userRepository.findByEmailAddress(customerEmail);
        if(Optional.ofNullable(customer).isEmpty()){
            code = ResponseCode.RECORD_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        // Create a new Beneficiary
        CustomerBeneficiary customerBeneficiary = new CustomerBeneficiary();
        customerBeneficiary.setCountry(requestPayload.getCountry());
        customerBeneficiary.setOwnerCustomerEmail(customerEmail);
        customerBeneficiary.setOwnerCustomerName(customer.getName());
        customerBeneficiary.setAccountName(requestPayload.getAccountName());
        customerBeneficiary.setOwnerCustomerPhone(customer.getMobileNumber());
        customerBeneficiary.setEmail(requestPayload.getEmail());
        customerBeneficiary.setName(requestPayload.getName());
        customerBeneficiary.setPhone(requestPayload.getPhone());
        customerBeneficiary.setCountryCode(requestPayload.getCountryCode());
        customerBeneficiary.setAccountNumber(requestPayload.getAccountNumber());
        customerBeneficiary.setCreatedBy(Creator.USER.name());
        customerBeneficiary.setCountry(requestPayload.getCountry());
        customerBeneficiary.setRoutingNumber(requestPayload.getRoutingNumber());
        customerBeneficiary.setCreatedAt(LocalDateTime.now().toString());
        customerBeneficiary.setUpdatedAt(LocalDateTime.now().toString());
        customerBeneficiary.setUpdatedBy(Creator.USER.name());
        customerBeneficiary.setStatus(ModelStatus.ACTIVE.name());
        CustomerBeneficiary savedBeneficiary = beneficiaryRepository.saveAndFlush(customerBeneficiary);

        // Return response to the client.
        code = ResponseCode.SUCCESS;
        responsePayload.setResponseCode(code);
        responsePayload.setResponseMessage(messageProvider.getMessage(code));
        responsePayload.setResponseData(savedBeneficiary);
        return responsePayload;
    }


    @Override
    public CustomerBeneficiaryListResponsePayload processGetCustomerBeneficiaryList(String authToken){
        CustomerBeneficiaryListResponsePayload responsePayload = new CustomerBeneficiaryListResponsePayload();
        String code;

        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));
        GmsUser customer = userRepository.findByEmailAddress(email);
        if(Optional.ofNullable(customer).isEmpty()){
            code = ResponseCode.RECORD_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        List<CustomerBeneficiary> beneficiaries = beneficiaryRepository.findByOwnerCustomerEmail(email);
        code = ResponseCode.SUCCESS;
        responsePayload.setResponseCode(code);
        responsePayload.setResponseMessage(messageProvider.getMessage(code));
        responsePayload.setResponseData(beneficiaries);
        return responsePayload;
    }


    @Override
    public Double getExchangeRate(String fromCurrency, String toCurrency) {
        BaseResponse baseResponse = transactionIntegrationService.processExchangeRate(fromCurrency, toCurrency);

        GmsExchangeRate exchangeRate = exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
        double result;
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            Double currentRate = Double.parseDouble(baseResponse.getOtherDetailsJson());
            result = currentRate;
            if(exchangeRate == null){
                exchangeRate = new GmsExchangeRate();
                exchangeRate.setFromCurrency(fromCurrency);
                exchangeRate.setToCurrency(toCurrency);
                exchangeRate.setCreatedAt(LocalDateTime.now());
                exchangeRate.setCreatedBy(Creator.SYSTEM.name());
                exchangeRate.setStatus(ModelStatus.ACTIVE.name());
                exchangeRate.setUuid(UUID.randomUUID().toString());
            }
            exchangeRate.setCurrentRate(BigDecimalUtil.from(currentRate));
            exchangeRate.setUpdatedAt(LocalDateTime.now());
            GmsExchangeRate savedExchangeRate = exchangeRateRepository.saveAndFlush(exchangeRate);

            log.info("Saved Exchange rate: {}", savedExchangeRate);
            log.info("Updated exchange rate between currencies: {} and {} with rate {}", fromCurrency, toCurrency, currentRate);
        }
        else{
            if(exchangeRate != null) {
                log.error("Could not get live exchange rate from web service. Using cached exchange rate...");
                result = exchangeRate.getCurrentRate().doubleValue();
            }
            else {
                log.error("Could not get live exchange rate from web service and could not also find cached rate. Using the default rate from internal factory...");
                result = ExchangeRateFactory.getDefaultExchangeRate(fromCurrency, toCurrency);
            }
        }
        return result;
    }


    private List<TransactionHistoryResponseData> getOutflowTransactionHistory(TransactionHistoryRequestPayload requestPayload, String email){

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        List<TransactionEntry> transactionEntries = transactionEntryRepository.findByCustomerEmail(email);
        if(requestPayload.getStartDate() != null){
            LocalDate startDate = LocalDate.parse(requestPayload.getStartDate());
            startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);

            transactionEntries = transactionEntries.stream().filter(e -> {
                LocalDateTime eTime = LocalDateTime.parse(e.getCreatedAt());
                return eTime.isEqual(startDateTime) || eTime.isAfter(startDateTime);
            }).collect(Collectors.toList());
        }

        if(requestPayload.getEndDate() != null){
            LocalDate endDate = LocalDate.parse(requestPayload.getEndDate());
            endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

            transactionEntries = transactionEntries.stream().filter(e -> {
                LocalDateTime eTime = LocalDateTime.parse(e.getCreatedAt());
                return eTime.isEqual(endDateTime) || eTime.isBefore(endDateTime);
            }).collect(Collectors.toList());
        }

        return transactionEntries.stream().map(this::buildHistoryDataFromTransactionEntry).collect(Collectors.toList());

    }


    private List<TransactionHistoryResponseData> getInflowTransactionHistory(TransactionHistoryRequestPayload requestPayload, String email){
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        List<AccountDeposit> depositEntries = accountDepositRepository.findBySourceCustomerEmail(email);
        if(requestPayload.getStartDate() != null){
            LocalDate startDate = LocalDate.parse(requestPayload.getStartDate());
            startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);

            depositEntries = depositEntries.stream().filter(e -> {
                LocalDateTime eTime = LocalDateTime.parse(e.getCreatedAt());
                return eTime.isEqual(startDateTime) || eTime.isAfter(startDateTime);
            }).collect(Collectors.toList());
        }

        if(requestPayload.getEndDate() != null){
            LocalDate endDate = LocalDate.parse(requestPayload.getEndDate());
            endDateTime = LocalDateTime.of(endDate, LocalTime.MIDNIGHT);

            depositEntries = depositEntries.stream().filter(e -> {
                LocalDateTime eTime = LocalDateTime.parse(e.getCreatedAt());
                return eTime.isEqual(endDateTime) || eTime.isBefore(endDateTime);
            }).collect(Collectors.toList());
        }

        return depositEntries.stream().map(this::buildHistoryDataFromDepositEntry).collect(Collectors.toList());
    }


    private TransactionHistoryResponseData buildHistoryDataFromTransactionEntry(TransactionEntry e){
        TransactionHistoryResponseData h = new TransactionHistoryResponseData();
        h.setTransactionType(e.getTransactionType());
        h.setCreatedAt(e.getCreatedAt());
        h.setUpdatedAt(e.getUpdatedAt());
        h.setStatus(e.getExternalStatus());
        h.setSourceWalletId(e.getSourceWalletId());
        h.setBeneficiaryName(e.getCustomerBeneficiaryName());
        h.setBeneficiaryAccount(e.getCustomerBeneficiaryAccount());
        h.setBeneficiaryPhone(e.getCustomerBeneficiaryPhone());
        h.setInternalReference(e.getInternalRef());
        h.setExternalReference(e.getExternalRef());
        h.setHash(e.getTransactionHash());
        h.setAmount(e.getTransactionAmount());
        h.setFee(e.getTransactionFee());
        h.setTotalAmount(e.getTransactionTotalAmount());
        h.setCurrency(e.getCurrency() == null ? "USD" : e.getCurrency());
        h.setTransactionEffect(HistoryType.WITHDRAWAL.name());
        h.setWalletBalance(e.getOwnerWalletBalance() == null ? "0.00" : e.getOwnerWalletBalance());
        h.setDescription(e.getDescription());
        return h;
    }


    private TransactionHistoryResponseData buildHistoryDataFromDepositEntry(AccountDeposit e){
        TransactionHistoryResponseData h = new TransactionHistoryResponseData();
        h.setTransactionType(e.getTransactionType() == null ? HistoryType.DEPOSIT.name() : e.getTransactionType());
        h.setCreatedAt(e.getCreatedAt());
        h.setUpdatedAt(e.getUpdatedAt());
        h.setStatus(e.getStatus());
        h.setSourceWalletId(e.getSourceWalletId());
        h.setBeneficiaryName(e.getBeneficiaryAccountName());
        h.setBeneficiaryAccount(e.getBeneficiaryWalletOrAccountId());
        h.setBeneficiaryPhone(StringValues.EMPTY_STRING);
        h.setInternalReference(e.getInternalRef());
        h.setExternalReference(e.getDepositId());
        h.setHash(StringValues.EMPTY_STRING);
        h.setAmount(e.getAmount());
        h.setFee(StringValues.EMPTY_STRING);
        h.setTotalAmount(e.getAmount());
        h.setCurrency(e.getCurrency() == null ? "USD" : e.getCurrency());
        h.setTransactionEffect(HistoryType.DEPOSIT.name());
        h.setWalletBalance(e.getOwnerWalletBalance() == null ? "0.00" : e.getOwnerWalletBalance());
        return h;
    }


    private TransactionPojo getTransactionPojoForVendor(String totalAmount){
        String activeVendor = gmsDAO.getActiveVendor();
        MasterWallet masterWallet = gmsDAO.getMasterWallet();
        MasterBankAccount masterBankAccount = gmsDAO.getMasterBankAccount();

        TransactionPojo transactionPojo = new TransactionPojo();
        transactionPojo.setMasterWallet(masterWallet);
        transactionPojo.setMasterBankAccount(masterBankAccount);

        if(activeVendor.equalsIgnoreCase(Vendor.CIRCLE.name())){
            String destinationBlockChainAddress = env.getProperty("third-party.alquimia.blockchain.address");
            transactionPojo.setAmount(totalAmount);
            transactionPojo.setDestinationBlockchainAddress(destinationBlockChainAddress);
            transactionPojo.setCurrency("USD");
            transactionPojo.setChain(Blockchain.ETH.name());
        }

        return transactionPojo;
    }


    private String getInternalTransactionFee(){
        GmsParam param = gmsDAO.getParamByKey("INTERNAL_TRANSACTION_FEE_PERCENT");
        if(param == null){
            param = new GmsParam();
            param.setParamDesc("Internal transaction fee");
            param.setCreatedBy(Creator.SYSTEM.name());
            param.setParamKey("INTERNAL_TRANSACTION_FEE_PERCENT");
            param.setParamValue("3.00");
            param.setCreatedAt(LocalDateTime.now().toString());
            param.setUpdatedAt(LocalDateTime.now().toString());
            param.setUpdatedBy(Creator.SYSTEM.name());

            GmsParam savedParam = gmsDAO.saveParam(param);
            return savedParam.getParamValue();
        }

        return param.getParamValue();
    }


    private String cleanToken(String authToken){
        return authToken.startsWith(StringValues.AUTH_HEADER_BEARER_KEY) ? authToken.replace(StringValues.AUTH_HEADER_BEARER_KEY, StringValues.EMPTY_STRING).trim() : authToken.trim();
    }
}
