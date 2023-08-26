package com.gms.alquimiapay.modules.cheque.service;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.account.model.AccountDeposit;
import com.gms.alquimiapay.modules.account.repository.IAccountDepositRepository;
import com.gms.alquimiapay.modules.audit.service.IAdminAuditService;
import com.gms.alquimiapay.modules.cheque.constant.ChequeStatus;
import com.gms.alquimiapay.modules.cheque.model.Cheque;
import com.gms.alquimiapay.modules.cheque.payload.SingleChequeResponsePayload;
import com.gms.alquimiapay.modules.cheque.payload.data.ChequeFileData;
import com.gms.alquimiapay.modules.cheque.payload.data.ChequeListResponsePayload;
import com.gms.alquimiapay.modules.cheque.payload.data.ChequeResponseData;
import com.gms.alquimiapay.modules.cheque.payload.request.ChequeDepositRequestPayload;
import com.gms.alquimiapay.modules.cheque.payload.request.ChequeListRequestPayload;
import com.gms.alquimiapay.modules.cheque.payload.request.SubmitDepositChequeRequestPayload;
import com.gms.alquimiapay.modules.cheque.payload.response.SubmitChequeDepositResponsePayload;
import com.gms.alquimiapay.modules.cheque.repository.IChequeRepository;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.storage.model.FileUpload;
import com.gms.alquimiapay.modules.storage.model.FileUploadContent;
import com.gms.alquimiapay.modules.storage.payload.FileUploadListRequestPayload;
import com.gms.alquimiapay.modules.storage.payload.FileUploadListResponsePayload;
import com.gms.alquimiapay.modules.storage.repository.IFileUploadContentRepository;
import com.gms.alquimiapay.modules.storage.repository.IFileUploadRepository;
import com.gms.alquimiapay.modules.storage.service.FtpClientService;
import com.gms.alquimiapay.modules.storage.service.IFtpClientService;
import com.gms.alquimiapay.modules.transaction.constant.HistoryType;
import com.gms.alquimiapay.modules.user.model.GmsAdmin;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.modules.user.service.IAdminUserService;
import com.gms.alquimiapay.modules.wallet.constant.WalletBalanceType;
import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletOperationResult;
import com.gms.alquimiapay.modules.wallet.repository.IGmsWalletCacheRepository;
import com.gms.alquimiapay.modules.wallet.service.IWalletService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.util.BigDecimalUtil;
import com.gms.alquimiapay.util.EmailMessenger;
import com.gms.alquimiapay.util.JwtUtil;
import com.gms.alquimiapay.validation.GenericValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ChequeDepositService implements IChequeDepositService
{

    private final JwtUtil jwtUtil;
    private final IUserRepository userRepository;
    private final IChequeRepository chequeRepository;
    private final MessageProvider messageProvider;
    private final IFtpClientService ftpClientService;
    private final GenericValidator validator;
    private final IAdminUserService adminUserService;
    private final IGmsWalletCacheRepository walletCacheRepository;
    private final IAccountDepositRepository accountDepositRepository;
    private final IWalletService walletService;
    private final IFileUploadContentRepository fileUploadContentRepository;
    private final IFileUploadRepository fileUploadRepository;
    private final EmailMessenger emailMessenger;
    private final IAdminAuditService adminAuditService;


    @Value("${gms.deposit.cheque.fee.percent}")
    private Double chequeDepositFeePercent;


    private final static String CHEQUE_REMOTE_BASE_DIRECTORY = "cheques";

    public ChequeDepositService(
            JwtUtil jwtUtil,
            IUserRepository userRepository,
            IChequeRepository chequeRepository,
            MessageProvider messageProvider,
            IFtpClientService ftpClientService,
            GenericValidator validator,
            IAdminUserService adminUserService,
            IGmsWalletCacheRepository walletCacheRepository,
            IAccountDepositRepository accountDepositRepository,
            @Qualifier(value = QualifierService.LOCAL_WALLET_SERVICE) IWalletService walletService,
            IFileUploadContentRepository fileUploadContentRepository,
            IFileUploadRepository fileUploadRepository,
            EmailMessenger emailMessenger,
            IAdminAuditService adminAuditService)
    {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.chequeRepository = chequeRepository;
        this.messageProvider = messageProvider;
        this.ftpClientService = ftpClientService;
        this.validator = validator;
        this.adminUserService = adminUserService;
        this.walletCacheRepository = walletCacheRepository;
        this.accountDepositRepository = accountDepositRepository;
        this.walletService = walletService;
        this.fileUploadContentRepository = fileUploadContentRepository;
        this.fileUploadRepository = fileUploadRepository;
        this.emailMessenger = emailMessenger;
        this.adminAuditService = adminAuditService;
    }

    @Override
    public SubmitChequeDepositResponsePayload processChequeSubmission(String token, SubmitDepositChequeRequestPayload requestPayload) {
        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(token));
        GmsUser user = userRepository.findByEmailAddress(email);

        // Establish the default System error response.
        SubmitChequeDepositResponsePayload responsePayload = new SubmitChequeDepositResponsePayload();
        String code = ResponseCode.SYSTEM_ERROR;
        responsePayload.setResponseCode(code);
        responsePayload.setResponseMessage(messageProvider.getMessage(code));

        // Validate the cheque file data sent from the client.
        ChequeFileData data = requestPayload.getFileData();
        validator.doModelValidationThrowException(data);

        try{
            // Check that the user exist in the system.
            if(user == null){
                code = ResponseCode.RECORD_NOT_FOUND;
                responsePayload.setResponseCode(code);
                responsePayload.setResponseMessage(messageProvider.getMessage(code));
                return responsePayload;
            }

            // Create the file upload data.
            String filenameWithExt = data.getFileName().concat(resolveExtension(data.getExtension()));
            String uniqueFilenameWithExt = String.valueOf(System.currentTimeMillis()).concat(StringValues.HYPHEN).concat(filenameWithExt);
            FileUpload fileUpload = FileUpload.builder()
                    .fileName(data.getFileName())
                    .fileType(data.getFileType())
                    .extension(data.getExtension())
                    .filenameWithExtension(filenameWithExt)
                    .uniqueFileNameWithExtension(uniqueFilenameWithExt)
                    .mimeType(data.getMimeType())
                    .remoteParentDirPath(CHEQUE_REMOTE_BASE_DIRECTORY.concat(getDirectoryNameForChequeUpload()))
                    .uuid("file_".concat(UUID.randomUUID().toString()))
                    .ownerEmail(email)
                    .createdAt(LocalDateTime.now().toString())
                    .updatedAt(LocalDateTime.now().toString())
                    .build();
            FileUpload saveFileUpload = fileUploadRepository.saveAndFlush(fileUpload);

            // Save the file upload content to the database
            FileUploadContent content = FileUploadContent.builder()
                    .base64Content(data.getBase64Content())
                    .fileUploadId(saveFileUpload.getUuid())
                    .uuid("file_content_".concat(UUID.randomUUID().toString()))
                    .build();
            FileUploadContent savedContent = fileUploadContentRepository.saveAndFlush(content);

            // Persist the cheque data
            Cheque cheque = Cheque.builder()
                    .chequeAmount(requestPayload.getChequeAmount())
                    .chequeNumber(requestPayload.getChequeNumber())
                    .createdAt(LocalDateTime.now().toString())
                    .updatedAt(LocalDateTime.now().toString())
                    .accountNumber(requestPayload.getAccountNumber())
                    .accountName(requestPayload.getAccountName())
                    .bank(requestPayload.getBank())
                    .ownerEmail(email)
                    .routingNumber(requestPayload.getRoutingNumber())
                    .uuid("cheque_".concat(UUID.randomUUID().toString()))
                    .fileUUID(saveFileUpload.getUuid())
                    .currency(requestPayload.getCurrency().toUpperCase())
                    .status(ChequeStatus.PENDING.name())
                    .build();
            Cheque savedCheque = chequeRepository.saveAndFlush(cheque);

            // Now save the file both locally and remotely
            BaseResponse response = ftpClientService.processFileStorage(savedContent.getBase64Content(), saveFileUpload, true);
            if(response.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
                ChequeResponseData responseData = new ChequeResponseData();
                responseData.setId(savedCheque.getUuid());
                responseData.setPublicLink(fileUpload.getPublicLink());
                responseData.setStatus(ChequeStatus.PENDING.name());
                responseData.setCreatedAt(LocalDateTime.now().toString());
                responseData.setRemotelySaved(true);
                responseData.setProposedAmount(requestPayload.getChequeAmount().toString());
                responseData.setCurrency(requestPayload.getCurrency().toUpperCase());

                responsePayload.setResponseCode(response.getResponseCode());
                responsePayload.setResponseMessage(response.getResponseMessage());
                responsePayload.setResponseData(responseData);

                // Send acknowledgement email
                CompletableFuture.runAsync(() -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("fullname", user.getName());
                    map.put("amount", cheque.getChequeAmount().toString());
                    map.put("currency", cheque.getCurrency());
                    emailMessenger.sendMessageWithData(user.getEmailAddress(), "cheque-submission-request", "Confirmación de envío de cheques", map);
                });

                return responsePayload;
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception while trying to submit cheque order: {}", e.getMessage());
        }

        return responsePayload;
    }


    @Override
    public ChequeListResponsePayload processChequeListForUser(String token, String startDate, String endDate, String status){
        ChequeListResponsePayload responsePayload = new ChequeListResponsePayload();
        String code;

        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(token));
        GmsUser user = userRepository.findByEmailAddress(email);

        // Validate the start and the end-date
        if(endDate != null && !endDate.isBlank() && !endDate.isEmpty()) {
            if (LocalDate.parse(startDate).isAfter(LocalDate.parse(endDate))) {
                code = ResponseCode.BAD_MODEL;
                responsePayload.setResponseCode(code);
                responsePayload.setResponseMessage("startDate cannot be greater than endDate");
                return responsePayload;
            }
        }

        // Check that the user exist in the system.
        if(user == null){
            code = ResponseCode.RECORD_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        FileUploadListResponsePayload listResponsePayload = ftpClientService.processFileUploadListForUser(email, startDate, endDate, CHEQUE_REMOTE_BASE_DIRECTORY);

        List<FileUpload> fileUploads = listResponsePayload.getFileUploads();

        if(status != null && !status.isEmpty() && !status.isBlank()){
            fileUploads = fileUploads.stream().filter(up -> {
                Cheque cheque = chequeRepository.findByFileUUID(up.getUuid());
                return cheque != null && cheque.getStatus() != null && cheque.getStatus().equalsIgnoreCase(status);
            }).collect(Collectors.toList());
        }

        List<ChequeResponseData> data = fileUploads.stream().map(u -> {
            ChequeResponseData r = new ChequeResponseData();
            Cheque cheque = chequeRepository.findByFileUUID(u.getUuid());
            if(cheque != null) {
                r.setId(cheque.getUuid());
                r.setCurrency(cheque.getCurrency());
                r.setStatus(cheque.getStatus());
                r.setProposedAmount(cheque.getChequeAmount().toString());
                r.setCreatedAt(cheque.getCreatedAt());
                r.setRemotelySaved(u.isRemotelySaved());
                r.setPublicLink(u.getPublicLink());
                if (u.getPublicLink() == null || u.getPublicLink().isEmpty() || u.getPublicLink().isBlank() || !u.isRemotelySaved()) {
                    FileUploadContent content = fileUploadContentRepository.findByFileUploadId(u.getUuid());
                    r.setBase64Content(content.getBase64Content());
                } else {
                    r.setBase64Content(StringValues.EMPTY_STRING);
                }
            }
            return r;
        }).collect(Collectors.toList());

        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setResponseMessage(messageProvider.getMessage(ResponseCode.SUCCESS));
        responsePayload.setResponseData(data);
        return responsePayload;
    }

    /**
     *      ADMIN SECTION OF CHEQUE PROCESSING
     */
    @Override
    public ChequeListResponsePayload processChequeListForAdmin(ChequeListRequestPayload requestPayload){

        ChequeListResponsePayload responsePayload = new ChequeListResponsePayload();
        String operationMessage = "Fetch cheque List";

        // Validate that the request is coming from the continuation session of the admin.
        BaseResponse adminSessionContinuation = adminUserService.processAdminUserSessionContinuation(requestPayload.getAdminUser(), requestPayload.getAdminPasscode());
        Object adminObject = adminSessionContinuation.getOtherDetails();
        GmsAdmin admin = adminObject == null ? new GmsAdmin() : (GmsAdmin) adminObject;
        if(!adminSessionContinuation.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            responsePayload.setResponseCode(adminSessionContinuation.getResponseCode());
            responsePayload.setResponseMessage(adminSessionContinuation.getResponseMessage());
            adminAuditService.saveAudit(responsePayload, responsePayload.getResponseCode(), operationMessage, admin);
            return responsePayload;
        }

        // Validate the start and the end-date
        if(requestPayload.getEndDate() != null && !requestPayload.getEndDate().isEmpty() && !requestPayload.getEndDate().isBlank()) {
            if (LocalDate.parse(requestPayload.getStartDate()).isAfter(LocalDate.parse(requestPayload.getEndDate()))) {
                responsePayload.setResponseCode(ResponseCode.BAD_MODEL);
                responsePayload.setResponseMessage("startDate cannot be greater than endDate");
                return responsePayload;
            }
        }

        FileUploadListRequestPayload request = new FileUploadListRequestPayload();
        request.setStartDate(requestPayload.getStartDate());
        request.setEndDate(requestPayload.getEndDate());
        request.setFolderPrefix(CHEQUE_REMOTE_BASE_DIRECTORY);
        List<FileUpload> fileUploads = ftpClientService.processFileUploadListForAdmin(request).getFileUploads();

        if(requestPayload.getStatus() != null && !requestPayload.getStatus().isEmpty() && !requestPayload.getStatus().isBlank()){
            fileUploads = fileUploads.stream().filter(up -> {
               Cheque cheque = chequeRepository.findByFileUUID(up.getUuid());
               return cheque != null && cheque.getStatus() != null && cheque.getStatus().equalsIgnoreCase(requestPayload.getStatus());
            }).collect(Collectors.toList());
        }

        List<ChequeResponseData> data = fileUploads.stream().map(u -> {
            ChequeResponseData r = new ChequeResponseData();
            Cheque cheque = chequeRepository.findByFileUUID(u.getUuid());
            GmsUser user = userRepository.findByEmailAddress(u.getOwnerEmail());
            if(cheque != null) {
                r.setId(cheque.getUuid());
                r.setCurrency(cheque.getCurrency());
                r.setStatus(cheque.getStatus());
                r.setProposedAmount(cheque.getChequeAmount().toString());
                r.setCreatedAt(cheque.getCreatedAt());
                r.setRemotelySaved(u.isRemotelySaved());
                r.setPublicLink(u.getPublicLink());
                r.setOwnerUsername(user.getName());
                r.setOwnerEmail(u.getOwnerEmail());
                if (u.getPublicLink() == null || u.getPublicLink().isEmpty() || u.getPublicLink().isBlank() || !u.isRemotelySaved()) {
                    FileUploadContent content = fileUploadContentRepository.findByFileUploadId(u.getUuid());
                    r.setBase64Content(content.getBase64Content());
                } else {
                    r.setBase64Content(StringValues.EMPTY_STRING);
                }
            }
            return r;
        }).collect(Collectors.toList());

        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setResponseMessage(messageProvider.getMessage(ResponseCode.SUCCESS));
        responsePayload.setResponseData(data);
        adminAuditService.saveAudit(responsePayload, responsePayload.getResponseCode(), operationMessage, admin);
        return responsePayload;
    }


    @SuppressWarnings("DuplicatedCode")
    @Override
    public BaseResponse processChequeDepositVerification(ChequeDepositRequestPayload requestPayload){
        String operationMessage = "Cheque Approval";

        // Validate that the request is coming from the continuation session of the admin.
        BaseResponse adminSessionContinuation = adminUserService.processAdminUserSessionContinuation(requestPayload.getAdminUser(), requestPayload.getAdminPasscode());
        Object adminObject = adminSessionContinuation.getOtherDetails();
        GmsAdmin admin = adminObject == null ? new GmsAdmin() : (GmsAdmin) adminObject;
        if(!adminSessionContinuation.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            adminAuditService.saveAudit(adminSessionContinuation, adminSessionContinuation.getResponseCode(), operationMessage, admin);
            return adminSessionContinuation;
        }

        BaseResponse response = new BaseResponse();
        String code;

        // Get the cheque upload from the database.
        Cheque cheque = chequeRepository.findByUuid(requestPayload.getChequeId());
        if(cheque == null){
            code = ResponseCode.CHEQUE_RECORD_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin);
            return response;
        }

        // Verify that the Cheque has not yet been approved.
        if(cheque.getStatus().equalsIgnoreCase(ChequeStatus.APPROVED.name())){
            code = ResponseCode.CHEQUE_RECORD_ALREADY_APPROVED;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin);
            return response;
        }

        // Verify that the cheque has not yet been rejected.
        if(cheque.getStatus().equalsIgnoreCase(ChequeStatus.REJECTED.name())){
            code = ResponseCode.CHEQUE_RECORD_ALREADY_REJECTED;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin);
            return response;
        }

        // All validation passed. Credit the wallet of the user.
        String ownerEmail = cheque.getOwnerEmail();
        GmsUser user = userRepository.findByEmailAddress(ownerEmail);
        String estimatedAmountLessFee = getAmountLessDepositFee(requestPayload.getEstimatedAmount());
        GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(ownerEmail);
        WalletOperationResult walletOperationResultAvailableBalance = walletService.processCreditWalletRequest(estimatedAmountLessFee, walletCache, WalletBalanceType.AVAILABLE_BALANCE);
        if(walletOperationResultAvailableBalance.getHasError()){
            response.setResponseCode(walletOperationResultAvailableBalance.getResponseCode());
            response.setResponseMessage(walletOperationResultAvailableBalance.getResponseMessage());
            adminAuditService.saveAudit(response, response.getResponseCode(), operationMessage, admin);
            return response;
        }

        WalletOperationResult walletOperationResultPendingBalance = walletService.processCreditWalletRequest(estimatedAmountLessFee, walletCache, WalletBalanceType.PENDING_BALANCE);
        if(walletOperationResultPendingBalance.getHasError()){
            response.setResponseCode(walletOperationResultAvailableBalance.getResponseCode());
            response.setResponseMessage(walletOperationResultAvailableBalance.getResponseMessage());
            adminAuditService.saveAudit(response, response.getResponseCode(), operationMessage, admin);
            return response;
        }

        // Update the status of the cheque entity
        cheque.setStatus(ChequeStatus.APPROVED.name());
        cheque.setUpdatedAt(LocalDateTime.now().toString());
        Cheque updatedCheque = chequeRepository.saveAndFlush(cheque);

        // Save the deposit
        GmsUser customer = userRepository.findByEmailAddress(ownerEmail);
        AccountDeposit deposit = new AccountDeposit();
        deposit.setCreatedAt(LocalDateTime.now().toString());
        deposit.setDepositId(UUID.randomUUID().toString());
        deposit.setStatus(ModelStatus.COMPLETE.name());
        deposit.setAmount(estimatedAmountLessFee);
        deposit.setUpdatedAt(deposit.getCreatedAt());
        deposit.setSourceWalletId("");
        deposit.setBeneficiaryAccountName(cheque.getAccountName());
        deposit.setBeneficiaryType(customer.getUserType());
        deposit.setBeneficiaryWalletOrAccountId(cheque.getAccountNumber());
        deposit.setCurrency(cheque.getCurrency());
        deposit.setInternalRef(deposit.getDepositId());
        deposit.setOwnerWalletBalance(walletCache.getAvailableBalance());
        deposit.setTransactionType(HistoryType.DEPOSIT.name());
        deposit.setSourceType("CHEQUE");
        deposit.setSourceCustomerName(customer.getName());
        deposit.setSourceCustomerEmail(ownerEmail);
        AccountDeposit savedDeposit = accountDepositRepository.saveAndFlush(deposit);

        // Send approval email
        CompletableFuture.runAsync(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("fullname", user.getName());
            map.put("balance", walletCache.getAvailableBalance());
            emailMessenger.sendMessageWithData(user.getEmailAddress(), "cheque-submission-approval", "Comprobar aprobación de envío", map);
        });

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code).concat( "Cheque approved successfully."));
        adminAuditService.saveAudit(response, code, operationMessage, admin);
        return response;
    }


    @SuppressWarnings("DuplicatedCode")
    @Override
    public BaseResponse processChequeDepositRejection(ChequeDepositRequestPayload requestPayload){
        String operationMessage = "Cheque Rejection";

        // Validate that the request is coming from the continuation session of the admin.
        BaseResponse adminSessionContinuation = adminUserService.processAdminUserSessionContinuation(requestPayload.getAdminUser(), requestPayload.getAdminPasscode());
        Object adminObject = adminSessionContinuation.getOtherDetails();
        GmsAdmin admin = adminObject == null ? new GmsAdmin() : (GmsAdmin) adminObject;
        if(!adminSessionContinuation.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            adminAuditService.saveAudit(adminSessionContinuation, adminSessionContinuation.getResponseCode(), operationMessage, admin);
            return adminSessionContinuation;
        }

        BaseResponse response = new BaseResponse();
        String code;

        // Get the cheque upload from the database.
        Cheque cheque = chequeRepository.findByUuid(requestPayload.getChequeId());
        if(cheque == null){
            code = ResponseCode.CHEQUE_RECORD_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin);
            return response;
        }

        // Verify that the Cheque has not yet been approved.
        if(cheque.getStatus().equalsIgnoreCase(ChequeStatus.APPROVED.name())){
            code = ResponseCode.CHEQUE_RECORD_ALREADY_APPROVED;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin);
            return response;
        }

        // Verify that the cheque has not yet been rejected.
        if(cheque.getStatus().equalsIgnoreCase(ChequeStatus.REJECTED.name())){
            code = ResponseCode.CHEQUE_RECORD_ALREADY_REJECTED;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin);
            return response;
        }

        // All validation passed.
        // Update the status of the cheque entity
        cheque.setStatus(ChequeStatus.REJECTED.name());
        cheque.setUpdatedAt(LocalDateTime.now().toString());
        Cheque updatedCheque = chequeRepository.saveAndFlush(cheque);

        String estimatedAmountLessFee = getAmountLessDepositFee(requestPayload.getEstimatedAmount());
        String ownerEmail = cheque.getOwnerEmail();
        GmsUser customer = userRepository.findByEmailAddress(ownerEmail);
        GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(ownerEmail);

        // Save the deposit
        AccountDeposit deposit = new AccountDeposit();
        deposit.setCreatedAt(LocalDateTime.now().toString());
        deposit.setDepositId(UUID.randomUUID().toString());
        deposit.setStatus(ModelStatus.FAILED.name());
        deposit.setAmount(estimatedAmountLessFee);
        deposit.setUpdatedAt(deposit.getCreatedAt());
        deposit.setSourceWalletId("");
        deposit.setBeneficiaryAccountName(cheque.getAccountName());
        deposit.setBeneficiaryType(customer.getUserType());
        deposit.setBeneficiaryWalletOrAccountId(cheque.getAccountNumber());
        deposit.setCurrency(cheque.getCurrency());
        deposit.setInternalRef(deposit.getDepositId());
        deposit.setOwnerWalletBalance(walletCache.getAvailableBalance());
        deposit.setTransactionType(HistoryType.DEPOSIT.name());
        deposit.setSourceType("CHEQUE");
        deposit.setSourceCustomerName(customer.getName());
        deposit.setSourceCustomerEmail(ownerEmail);
        AccountDeposit savedDeposit = accountDepositRepository.saveAndFlush(deposit);

        // Send rejection email
        CompletableFuture.runAsync(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("fullname", customer.getName());
            emailMessenger.sendMessageWithData(customer.getEmailAddress(), "cheque-submission-rejection", "Comprobar envío rechazado", map);
        });

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code).concat(" Cheque rejected successfully."));
        adminAuditService.saveAudit(response, code, operationMessage, admin);
        return response;
    }

    @Override
    public SingleChequeResponsePayload processSingleChequeById(String chequeId){
        SingleChequeResponsePayload responsePayload = new SingleChequeResponsePayload();
        String code;

        Cheque cheque = chequeRepository.findByUuid(chequeId);
        if(cheque == null){
            code = ResponseCode.ENTITY_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        FileUpload fileUpload = ftpClientService.processSingleFileUploadById(cheque.getFileUUID());
        if(fileUpload == null){
            code = ResponseCode.ENTITY_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage("No corresponding local file entity found for cheque request. Please contact support or admin.");
            return responsePayload;
        }

        ChequeResponseData data = new ChequeResponseData();
        data.setId(chequeId);
        data.setPublicLink(fileUpload.getPublicLink());
        data.setRemotelySaved(fileUpload.isRemotelySaved());
        data.setStatus(cheque.getStatus());
        data.setProposedAmount(cheque.getChequeAmount().toString());
        data.setCurrency(cheque.getCurrency());
        if(fileUpload.getPublicLink().isBlank() || fileUpload.getPublicLink().isBlank() || !fileUpload.isRemotelySaved()){
            FileUploadContent content = fileUploadContentRepository.findByFileUploadId(fileUpload.getUuid());
            data.setBase64Content(content.getBase64Content());
        }else{
            data.setBase64Content(StringValues.EMPTY_STRING);
        }
        data.setCreatedAt(cheque.getCreatedAt());

        code = ResponseCode.SUCCESS;
        responsePayload.setResponseCode(code);
        responsePayload.setResponseMessage(messageProvider.getMessage(code));
        responsePayload.setResponseData(data);
        return responsePayload;
    }


    private String getAmountLessDepositFee(String amount){
        BigDecimal incomingAmount = BigDecimalUtil.from(amount);
        BigDecimal depositFeePercent = BigDecimalUtil.from(chequeDepositFeePercent);
        BigDecimal depositFee = depositFeePercent
                .divide(BigDecimalUtil.from(100D), 2, RoundingMode.HALF_UP)
                .multiply(incomingAmount);
        String result = incomingAmount.subtract(depositFee).toString();
        return BigDecimalUtil.from(result).toString();
    }

    private String resolveExtension(String extension){
        String result = extension.startsWith(StringValues.DOT) ? extension : StringValues.DOT.concat(extension.trim());
        return result.toLowerCase();
    }

    private String getDirectoryNameForChequeUpload(){
        LocalDate localDate = LocalDate.now(ZoneId.systemDefault());
        return String.join(StringValues.HYPHEN, String.valueOf(localDate.getYear()), FtpClientService.getParseableDateElementFormat(localDate.getMonthValue()));
    }

    private String cleanToken(String authToken){
        return authToken.startsWith(StringValues.AUTH_HEADER_BEARER_KEY) ? authToken.replace(StringValues.AUTH_HEADER_BEARER_KEY, StringValues.EMPTY_STRING).trim() : authToken.trim();
    }

}
