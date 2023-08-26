package com.gms.alquimiapay.modules.user.service.impl;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.*;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaAddress;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaIdentity;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaMemberShipData;
import com.gms.alquimiapay.integration.external.sila.dto.identity.response.KycCheckResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.identity.response.SilaEntityResponseDTO;
import com.gms.alquimiapay.integration.external.sila.model.SilaUser;
import com.gms.alquimiapay.integration.external.sila.repository.SilaUserRepository;
import com.gms.alquimiapay.integration.internal.identity.IIntegrationIdentityService;
import com.gms.alquimiapay.modules.generic.constant.HashAlgo;
import com.gms.alquimiapay.modules.generic.service.IGenericService;
import com.gms.alquimiapay.modules.kyc.constant.KycStatus;
import com.gms.alquimiapay.modules.kyc.constant.Vendor;
import com.gms.alquimiapay.modules.kyc.model.UserKycVerification;
import com.gms.alquimiapay.modules.kyc.model.UserUploadDocument;
import com.gms.alquimiapay.modules.kyc.repository.UserKycVerificationRepository;
import com.gms.alquimiapay.modules.kyc.repository.UserUploadDocumentRepository;
import com.gms.alquimiapay.modules.user.constants.UserType;
import com.gms.alquimiapay.modules.user.model.GmsSilaBusiness;
import com.gms.alquimiapay.modules.user.model.GmsSilaBusinessOfficer;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.model.GmsUserOtp;
import com.gms.alquimiapay.modules.user.payload.identity.request.TransactionPinChangeRequestPayload;
import com.gms.alquimiapay.modules.user.payload.identity.request.TransactionPinResetOtpVerificationRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.data.*;
import com.gms.alquimiapay.modules.user.payload.kyc.request.BusinessKycRegistrationRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.request.IndividualDocumentUploadRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.request.IndividualUserKycRegistrationRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.request.CertifyBeneficialOwnerRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.response.UserDetailsResponsePayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.BusinessOfficerRequestPayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.UnlinkBusinessMemberRequestPayload;
import com.gms.alquimiapay.modules.user.repository.IGmsSilaBusinessOfficerRepository;
import com.gms.alquimiapay.modules.user.repository.IGmsSilaBusinessRepository;
import com.gms.alquimiapay.modules.user.repository.IUserOtpRepository;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.modules.user.service.IUserIdentityService;
import com.gms.alquimiapay.modules.user.validation.UserServiceValidator;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.payload.OtpSendInfo;
import com.gms.alquimiapay.util.EmailMessenger;
import com.gms.alquimiapay.util.JwtUtil;
import com.gms.alquimiapay.util.OtpUtil;
import com.gms.alquimiapay.util.PasswordUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.gms.alquimiapay.modules.kyc.constant.SilaBusinessOfficer.*;
import static com.gms.alquimiapay.modules.kyc.constant.SilaOfficerStatus.LINKED;
import static com.gms.alquimiapay.modules.kyc.constant.SilaOfficerStatus.UNLINKED;

@Slf4j
@Service
public class UserIdentityService implements IUserIdentityService
{

    @Qualifier(QualifierValue.SILA_PARTY_IDENTITY_SERVICE)
    private final IIntegrationIdentityService iIntegrationIdentityService;
    private final IUserRepository iUserRepository;
    private final SilaUserRepository silaUserRepository;
    private final MessageProvider messageProvider;
    private final JwtUtil jwtUtil;
    private final EmailMessenger emailMessenger;
    private final IGenericService genericService;
    private final UserUploadDocumentRepository uploadDocumentRepository;
    private final UserKycVerificationRepository kycVerificationRepository;
    private final IGmsSilaBusinessRepository businessRepository;
    private final IGmsSilaBusinessOfficerRepository businessOfficerRepository;
    private final UserServiceValidator validator;
    private final PasswordUtil passwordUtil;
    private final OtpUtil otpUtil;
    private final IUserOtpRepository otpRepository;

    private static final Gson JSON = new Gson();

    @Autowired
    public UserIdentityService(
            IIntegrationIdentityService iIntegrationIdentityService,
            IUserRepository iUserRepository,
            SilaUserRepository silaUserRepository,
            MessageProvider messageProvider,
            JwtUtil jwtUtil,
            EmailMessenger emailMessenger,
            @Lazy IGenericService genericService,
            UserUploadDocumentRepository uploadDocumentRepository,
            UserKycVerificationRepository kycVerificationRepository,
            IGmsSilaBusinessRepository businessRepository,
            IGmsSilaBusinessOfficerRepository businessOfficerRepository,
            UserServiceValidator validator,
            PasswordUtil passwordUtil,
            OtpUtil otpUtil,
            IUserOtpRepository otpRepository) {
        this.iIntegrationIdentityService = iIntegrationIdentityService;
        this.iUserRepository = iUserRepository;
        this.silaUserRepository = silaUserRepository;
        this.messageProvider = messageProvider;
        this.jwtUtil = jwtUtil;
        this.emailMessenger = emailMessenger;
        this.genericService = genericService;
        this.uploadDocumentRepository = uploadDocumentRepository;
        this.kycVerificationRepository = kycVerificationRepository;
        this.businessRepository = businessRepository;
        this.businessOfficerRepository = businessOfficerRepository;
        this.validator = validator;
        this.passwordUtil = passwordUtil;
        this.otpUtil = otpUtil;
        this.otpRepository = otpRepository;
    }

    @Override
    public BaseResponse checkUserHandle(String userHandle, String token) {
        token = cleanToken(token);
        String userEmail = jwtUtil.getUserEmailFromJWTToken(cleanToken(token));
        GmsUser user = iUserRepository.findByEmailAddress(userEmail);

        BaseResponse baseResponse = iIntegrationIdentityService.checkUserHandle(userHandle);
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            SilaUser silaUser = silaUserRepository.findByGmsUserEmail(userEmail);
            if(silaUser == null){
                silaUser = new SilaUser();
                silaUser.setGmsUserEmail(userEmail);
                silaUser.setGmsUserId(user.getUserId());
                silaUser.setSilaUserHandle(userHandle);
                silaUserRepository.save(silaUser);
            }
            silaUser.setSilaUserHandle(userHandle);
            silaUserRepository.save(silaUser);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse processStartKycRequest(String authToken){
        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));
        GmsUser user = iUserRepository.findByEmailAddress(email);

        BaseResponse baseResponse = iIntegrationIdentityService.processKYCRequest(user);
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            if(user.getUserType().equalsIgnoreCase(UserType.BUSINESS.name())){
               CompletableFuture.runAsync(() -> {
                   // Send email to the customer that there KYC is under review.
                   Map<String, String> data = new HashMap<>();
                   data.put("lastName", user.getBusinessName());
                   data.put("firstName", StringValues.EMPTY_STRING);
                   String subject = "GMS Sure Trade Business user KYC initiation";
                   emailMessenger.sendMessageWithData(user.getBusinessEmail(), "kyc-initiate", subject, data);
               });
            }
            else if(user.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name())){
                CompletableFuture.runAsync(() -> {
                    // Send email to the user for a successful upload and request for kyc is under review.
                    Map<String, String> data= new HashMap<>();
                    data.put("lastName", user.getLastName());
                    data.put("firstName", user.getFirstName());
                    String subject = "GMS Sure Trade Individual KYC Initiation";
                    emailMessenger.sendMessageWithData(user.getEmailAddress(), "kyc-initiate", subject, data);
                });
            }
        }
        return baseResponse;
    }

    @Override
    public BaseResponse processCheckKycRequest(String authToken){
        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));
        GmsUser user = iUserRepository.findByEmailAddress(email);

        BaseResponse baseResponse = iIntegrationIdentityService.processKycRequestChecking(user);
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            log.info("KYC status checking was a success for email: {}", email);
            String responseJson = baseResponse.getOtherDetailsJson();
            KycCheckResponseDTO responseDTO = JSON.fromJson(responseJson, KycCheckResponseDTO.class);

            UserKycVerification kycVerification = kycVerificationRepository.findByUserEmail(email);
            kycVerification.setLogs(responseJson);
            kycVerification.setKycTier("2");
            kycVerification.setUpdatedAt(LocalDateTime.now().toString());
            kycVerification.setStatus(responseDTO.getVerificationStatus().toUpperCase());
            kycVerification.setExternalReference(responseDTO.getReference());
            kycVerificationRepository.saveAndFlush(kycVerification);
        }

        return baseResponse;
    }

    @Override
    public BaseResponse processIndividualUserRegistrationForKyc(IndividualUserKycRegistrationRequestPayload requestPayload, String token) {
        token = cleanToken(token);
        String userEmail = jwtUtil.getUserEmailFromJWTToken(cleanToken(token));
        GmsUser user = iUserRepository.findByEmailAddress(userEmail);

        BaseResponse responsePayload = new BaseResponse();

        // Ensure handle is set.
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(userEmail);
        if(silaUser == null || silaUser.getSilaUserHandle() == null || silaUser.getSilaUserHandle().isBlank() || silaUser.getSilaUserHandle().isEmpty()){
            String code = ResponseCode.USER_HANDLE_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        user.setUserType(UserType.INDIVIDUAL.name());
        user.setSsn(requestPayload.getSsn());
        user.setDateOfBirth(requestPayload.getDateOfBirth());
        user.setAddress(requestPayload.getStreetAddress());
        user.setAddressAlias(requestPayload.getAddressAlias());
        user.setCity(requestPayload.getCity());
        user.setState(requestPayload.getState());
        user.setCountry(requestPayload.getCountry());
        user.setZipCode(requestPayload.getZipCode());
        iUserRepository.saveAndFlush(user);

        BaseResponse baseResponse = iIntegrationIdentityService.processUserRegistrationToThirdParty(user);
        if(!baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            return baseResponse;
        }

        user.setRegisteredWithSila(true);
        iUserRepository.saveAndFlush(user);

        // Set the kyc entity for the user
        UserKycVerification kycVerification = kycVerificationRepository.findByUserEmail(userEmail);
        if(kycVerification == null){
            kycVerification = new UserKycVerification();
        }
        kycVerification.setUserEmail(userEmail);
        kycVerification.setVendor(Vendor.SILA.name());
        kycVerification.setCreatedAt(LocalDateTime.now().toString());
        kycVerification.setUpdatedAt(LocalDateTime.now().toString());
        kycVerification.setStatus(KycStatus.PENDING.name());
        kycVerification.setKycLevel("DOC_KYC");
        kycVerification.setExternalReference("");
        kycVerification.setInternalReference(UUID.randomUUID().toString());
        kycVerification.setKycTier("1");
        kycVerificationRepository.saveAndFlush(kycVerification);

        // Send message to user asynchronously informing them that their registration was successful with internal system but still pending with Sila.
        CompletableFuture.runAsync(() -> {
            String recipient = user.getEmailAddress();
            String emailFile = "kyc-reg";
            String subject = "GMS Sure Trade KYC Individual Registration";
            Map<String , String> data = new HashMap<>();
            data.put("lastName", user.getLastName());
            data.put("firstName", user.getFirstName());
            data.put("userType", user.getUserType());
            data.put("fullName", String.join(" ", user.getLastName(), user.getFirstName(), user.getMiddleName()));
            data.put("creationDate", user.getCreatedAt());
            data.put("kycDate", LocalDateTime.now().toString());
            data.put("status", KycStatus.PENDING.name());
            emailMessenger.sendMessageWithData(recipient, emailFile, subject, data);
        });

        CompletableFuture.runAsync(() -> {
            // process request for kyc on behalf of the individual user.
            if(user.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name())) {
                BaseResponse requestKycResponse = iIntegrationIdentityService.processKYCRequest(user);
                if (requestKycResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)) {
                    // Send email to the user for a successful upload and request for kyc is under review.
                    CompletableFuture.runAsync(() -> {
                        Map<String, String> data = new HashMap<>();
                        data.put("lastName", user.getLastName());
                        data.put("firstName", user.getFirstName());
                        String subject = "GMS Sure Trade Individual KYC Verification Initiation";
                        emailMessenger.sendMessageWithData(user.getEmailAddress(), "kyc-initiate", subject, data);
                    });
                }else{
                    // log the error
                    log.error("Error while trying to send KYC request on behalf of user of type INDIVIDUAL: {}", requestKycResponse.getOtherDetailsJson());
                }
            }
        });

        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessUserRegistrationForKyc(BusinessKycRegistrationRequestPayload requestPayload, String token) {
        token = cleanToken(token);
        String userEmail = jwtUtil.getUserEmailFromJWTToken(cleanToken(token));
        GmsUser user = iUserRepository.findByEmailAddress(userEmail);

        // Ensure handle is set.
        BaseResponse responsePayload = new BaseResponse();
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(userEmail);
        if(silaUser == null || silaUser.getSilaUserHandle() == null || silaUser.getSilaUserHandle().isBlank() || silaUser.getSilaUserHandle().isEmpty()){
            String code = ResponseCode.USER_HANDLE_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        user.setUserType(UserType.BUSINESS.name());
        user.setBusinessName(requestPayload.getLegalBusinessName());
        user.setBusinessNickName(requestPayload.getBusinessAlias());
        user.setBusinessWebsite(requestPayload.getBusinessWebsite());
        user.setBusinessEin(requestPayload.getBusinessEin());
        user.setBusinessIncDate(requestPayload.getDateOfInc());
        user.setDateOfBirth(requestPayload.getDateOfInc());
        user.setBusinessAddress(requestPayload.getBusinessAddress());
        user.setAddress(requestPayload.getBusinessAddress());
        user.setAddressAlias(requestPayload.getAddressAlias());
        user.setCity(requestPayload.getCity());
        user.setState(requestPayload.getState());
        user.setCountry(requestPayload.getCountry());
        user.setZipCode(requestPayload.getZipCode());
        user.setBusinessEmail(userEmail);
        user.setBusinessPhone(requestPayload.getBusinessPhoneNumber());
        user.setBusinessRegState(requestPayload.getRegistrationState());
        user.setBusinessTypeUUID(requestPayload.getBusinessUUID());
        user.setEmployerIdentificationNumber(requestPayload.getBusinessEin());
        user.setBusinessCategoryCode(requestPayload.getBusinessCategoryCode());
        GmsUser savedUser = iUserRepository.saveAndFlush(user);

        BaseResponse baseResponse = iIntegrationIdentityService.processUserRegistrationToThirdParty(savedUser);
        if(!baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            return baseResponse;
        }

        user.setRegisteredWithSila(true);
        iUserRepository.saveAndFlush(user);

        // Set the kyc entity for the user
        UserKycVerification kycVerification = new UserKycVerification();
        kycVerification.setUserEmail(userEmail);
        kycVerification.setVendor(Vendor.SILA.name());
        kycVerification.setCreatedAt(LocalDateTime.now().toString());
        kycVerification.setUpdatedAt(LocalDateTime.now().toString());
        kycVerification.setStatus(KycStatus.PENDING.name());
        kycVerification.setKycLevel("DOC_KYC");
        kycVerification.setExternalReference("");
        kycVerification.setInternalReference(UUID.randomUUID().toString());
        kycVerification.setKycTier("1");
        kycVerificationRepository.saveAndFlush(kycVerification);

        // Register the business entity.
        GmsSilaBusiness business = businessRepository.findByBusinessEmail(userEmail);
        if(business == null){
            business = new GmsSilaBusiness();
        }

        business.setBusinessHandle(silaUser.getSilaUserHandle());
        business.setBusinessName(requestPayload.getLegalBusinessName());
        business.setBusinessWebsite(requestPayload.getBusinessWebsite());
        business.setBusinessEmail(userEmail);
        business.setCreatedAt(LocalDateTime.now().toString());
        business.setUpdatedAt(LocalDateTime.now().toString());
        business.setRequiresCertification(requestPayload.isRequiresCertification());

        businessRepository.saveAndFlush(business);

        // Send message to user asynchronously informing them that their registration was successful with internal system but still pending with Sila.
        CompletableFuture.runAsync(() -> {
            String recipient = user.getBusinessEmail();
            String emailFile = "kyc-reg";
            String subject = "GMS Sure Trade KYC Business Registration";
            Map<String , String> data = new HashMap<>();
            data.put("lastName", user.getBusinessName());
            data.put("firstName", StringValues.EMPTY_STRING);
            data.put("userType", user.getUserType());
            data.put("fullName", user.getBusinessName());
            data.put("creationDate", user.getCreatedAt());
            data.put("kycDate", LocalDateTime.now().toString());
            data.put("status", KycStatus.PENDING.name());
            emailMessenger.sendMessageWithData(recipient, emailFile, subject, data);
        });

        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessAdministratorRegistration(BusinessOfficerRequestPayload requestPayload){
        GmsSilaBusiness business = businessRepository.findByBusinessEmail(requestPayload.getBusinessEmail());
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(requestPayload.getOfficerEmail());
        GmsUser businessUser = iUserRepository.findByEmailAddress(requestPayload.getBusinessEmail());

        // Check that the business exist and has registered with Sila.
        BaseResponse error = new BaseResponse();
        String code;
        if(business == null || businessUser == null || !businessUser.isRegisteredWithSila()){
            code = ResponseCode.ENTITY_NOT_REMOTELY_LINKED;
            error.setResponseCode(code);
            error.setResponseMessage(messageProvider.getMessage(code));
            return error;
        }

        // Check that the business has registered with Sila
        BaseResponse errorResponse = new BaseResponse();
        if(business.getBusinessHandle() == null){
            errorResponse.setResponseCode(ResponseCode.USER_HANDLE_NOT_FOUND);
            errorResponse.setResponseMessage("Business handle not found");
            return errorResponse;
        }

        if(silaUser == null || silaUser.getSilaUserHandle() == null){
            errorResponse.setResponseCode(ResponseCode.USER_HANDLE_NOT_FOUND);
            errorResponse.setResponseMessage("User handle not found");
            return errorResponse;
        }

        BaseResponse baseResponse = iIntegrationIdentityService.processBusinessAdministratorRegistration(business.getBusinessHandle(), silaUser.getSilaUserHandle());
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            // Save the administrator entity
            GmsSilaBusinessOfficer businessOfficer = new GmsSilaBusinessOfficer();
            businessOfficer.setBusinessId(business.getId());
            businessOfficer.setOfficerHandle(silaUser.getSilaUserHandle());
            businessOfficer.setOfficerRole(ADMINISTRATOR.name());
            businessOfficer.setEmail(silaUser.getGmsUserEmail());
            businessOfficer.setLinkedAt(LocalDateTime.now().toString());
            businessOfficer.setCreatedAt(LocalDateTime.now().toString());
            businessOfficer.setUpdatedAt(LocalDateTime.now().toString());
            businessOfficer.setStatus(LINKED.name());
            businessOfficerRepository.saveAndFlush(businessOfficer);

            // Send email to both the company and the administrator email about the update.
            CompletableFuture.runAsync(() -> {

            });
        }
        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessControllerOfficerRegistration(BusinessOfficerRequestPayload requestPayload){
        GmsSilaBusiness business = businessRepository.findByBusinessEmail(requestPayload.getBusinessEmail());
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(requestPayload.getOfficerEmail());

        BaseResponse errorResponse = new BaseResponse();
        String code;
        if(business == null || business.getBusinessHandle() == null){
            errorResponse.setResponseCode(ResponseCode.USER_HANDLE_NOT_FOUND);
            errorResponse.setResponseMessage("Business handle not found");
            return errorResponse;
        }

        if(silaUser == null || silaUser.getSilaUserHandle() == null){
            errorResponse.setResponseCode(ResponseCode.USER_HANDLE_NOT_FOUND);
            errorResponse.setResponseMessage("User handle not found");
            return errorResponse;
        }

        // Ensure that business has an administrator that is linked.
        GmsSilaBusinessOfficer adminOfficer = businessOfficerRepository.findByEmailAndOfficerRoleAndStatus(silaUser.getGmsUserEmail(), ADMINISTRATOR.name(), LINKED.name()).stream().filter(officer -> officer.getBusinessId() == business.getId()).findFirst().orElse(null);
        if(adminOfficer == null){
            code = ResponseCode.NO_BUSINESS_ADMIN;
            errorResponse.setResponseCode(code);
            errorResponse.setResponseMessage(messageProvider.getMessage(code));
            return errorResponse;
        }

        BaseResponse baseResponse = iIntegrationIdentityService.processBusinessControllingOfficerRegistration(business.getBusinessHandle(), silaUser.getSilaUserHandle(), adminOfficer.getOfficerHandle());
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            // Save the officer in the table
            GmsSilaBusinessOfficer officer = new GmsSilaBusinessOfficer();
            officer.setOfficerRole(CONTROLLING_OFFICER.name());
            officer.setOfficerHandle(silaUser.getSilaUserHandle());
            officer.setBusinessId(business.getId());
            officer.setEmail(silaUser.getGmsUserEmail());
            officer.setLinkedAt(LocalDateTime.now().toString());
            officer.setCreatedAt(LocalDateTime.now().toString());
            officer.setUpdatedAt(LocalDateTime.now().toString());
            officer.setStatus(LINKED.name());
            businessOfficerRepository.saveAndFlush(officer);

            // Send email to both the company and the administrator email about the update.
            CompletableFuture.runAsync(() -> {

            });
        }
        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessBeneficialOwnerRegistration(BusinessOfficerRequestPayload requestPayload){
        GmsSilaBusiness business = businessRepository.findByBusinessEmail(requestPayload.getBusinessEmail());
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(requestPayload.getOfficerEmail());

        BaseResponse errorResponse = new BaseResponse();
        String code;
        if(business == null || business.getBusinessHandle() == null){
            errorResponse.setResponseCode(ResponseCode.USER_HANDLE_NOT_FOUND);
            errorResponse.setResponseMessage("Business handle not found");
            return errorResponse;
        }

        List<GmsSilaBusinessOfficer> admins = businessOfficerRepository.findByBusinessIdAndStatus(business.getId(), LINKED.name());
        GmsSilaBusinessOfficer admin = admins.stream().findFirst().orElse(null);

        if(admin == null){
            errorResponse.setResponseCode(ResponseCode.NO_BUSINESS_ADMIN);
            errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
            return errorResponse;
        }

        if(silaUser == null || silaUser.getSilaUserHandle() == null){
            errorResponse.setResponseCode(ResponseCode.USER_HANDLE_NOT_FOUND);
            errorResponse.setResponseMessage("User handle not found");
            return errorResponse;
        }

        // Ensure that business has an administrator that is linked.
        GmsSilaBusinessOfficer adminOfficer = businessOfficerRepository.findByEmailAndOfficerRoleAndStatus(silaUser.getGmsUserEmail(), ADMINISTRATOR.name(), LINKED.name()).stream().filter(officer -> officer.getBusinessId() == business.getId()).findFirst().orElse(null);
        if(adminOfficer == null){
            code = ResponseCode.NO_BUSINESS_ADMIN;
            errorResponse.setResponseCode(code);
            errorResponse.setResponseMessage(messageProvider.getMessage(code));
            return errorResponse;
        }

        BaseResponse baseResponse = iIntegrationIdentityService.processBusinessBeneficialOwnerRegistration(business.getBusinessHandle(), silaUser.getSilaUserHandle(), admin.getOfficerHandle(), requestPayload.getDetails(), requestPayload.getOwnerStake());
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            // Save the officer in the table
            GmsSilaBusinessOfficer officer = new GmsSilaBusinessOfficer();
            officer.setOfficerRole(BENEFICIAL_OWNER.name());
            officer.setOfficerHandle(silaUser.getSilaUserHandle());
            officer.setBusinessId(business.getId());
            officer.setEmail(silaUser.getGmsUserEmail());
            officer.setLinkedAt(LocalDateTime.now().toString());
            officer.setCreatedAt(LocalDateTime.now().toString());
            officer.setUpdatedAt(LocalDateTime.now().toString());
            officer.setStatus(LINKED.name());
            businessOfficerRepository.saveAndFlush(officer);

            // Send email to both the company and the administrator email about the update.
            CompletableFuture.runAsync(() -> {

            });
        }
        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessMemberUnlinking(UnlinkBusinessMemberRequestPayload requestPayload){
        String officerEmail = requestPayload.getOfficerEmail();
        String businessEmail = requestPayload.getBusinessEmail();
        String role = requestPayload.getRole();

        SilaUser officerSila = silaUserRepository.findByGmsUserEmail(officerEmail);
        SilaUser businessSila = silaUserRepository.findByGmsUserEmail(businessEmail);
        GmsSilaBusiness business = businessRepository.findByBusinessEmail(businessEmail);

        BaseResponse response = iIntegrationIdentityService.processBusinessMemberUnlinking(officerSila.getSilaUserHandle(), businessSila.getSilaUserHandle(), role.toLowerCase());
        if(response.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            GmsSilaBusinessOfficer officer = businessOfficerRepository.findByEmailAndOfficerRoleAndStatus(officerEmail, role.toUpperCase(), LINKED.name()).stream().filter(f -> f.getBusinessId() == business.getId()).findFirst().orElse(null);
            if(officer != null){
                officer.setStatus(UNLINKED.name());
                officer.setUnlinkedAt(LocalDateTime.now().toString());
                businessOfficerRepository.saveAndFlush(officer);
            }
        }
        return response;
    }

    @Override
    public UserDetailsResponsePayload processFetchUserDetailsRequest(String authToken){
            String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));
            GmsUser user = iUserRepository.findByEmailAddress(email);

            UserDetailsResponsePayload responsePayload = new UserDetailsResponsePayload();
            String code;

            SilaUser silaUser = silaUserRepository.findByGmsUserEmail(email);
            if (silaUser == null || silaUser.getSilaUserHandle() == null || silaUser.getSilaUserHandle().isEmpty() || silaUser.getSilaUserHandle().isBlank()) {
                code = ResponseCode.USER_HANDLE_NOT_FOUND;
                responsePayload.setResponseCode(code);
                responsePayload.setResponseMessage(messageProvider.getMessage(code));
                return responsePayload;
            }

            BaseResponse baseResponse = iIntegrationIdentityService.processGetUserEntity(silaUser.getSilaUserHandle());
            SilaEntityResponseDTO responseDTO = (SilaEntityResponseDTO) baseResponse.getOtherDetails();

            // Build the entity
            UserEntity userEntity = new UserEntity();
            userEntity.setBirthdate(responseDTO.getEntity().getBirthdate());
            userEntity.setEntityName(responseDTO.getEntity().getEntityName());
            userEntity.setFirstName(responseDTO.getEntity().getFirstName());
            userEntity.setLastName(responseDTO.getEntity().getLastName());
            userEntity.setType(responseDTO.getEntityType());
            userEntity.setBusinessType(user.getBusinessType());
            userEntity.setBusinessTypeUUID(user.getBusinessTypeUUID());
            userEntity.setDoingBusinessAs(user.getBusinessNickName());
            userEntity.setBusinessWebsite(user.getBusinessWebsite());
            userEntity.setRelationship("");
            userEntity.setCreatedEpoch(responseDTO.getEntity().getCreatedEpoch());

            // Build the contact
            UserContact contact = new UserContact();
            contact.setContactAlias(user.getContactAlias());
            contact.setPhone(user.getMobileNumber());
            contact.setEmail(user.getEmailAddress());

            // Build the address
            SilaAddress silaAddress = responseDTO.getAddresses().get(0);
            UserAddress address = new UserAddress();
            address.setAddressAlias(user.getAddressAlias());
            address.setStreetAddress1(silaAddress.getStreetAddress1());
            address.setCity(silaAddress.getCity());
            address.setState(silaAddress.getState());
            address.setCountry(silaAddress.getCountry());
            address.setPostalCode(silaAddress.getPostalCode());
            address.setAddedEpoch(silaAddress.getAddedEpoch());
            address.setModifiedEpoch(silaAddress.getModifiedEpoch());
            address.setUuid(silaAddress.getUuid());
            address.setNickName(user.getAddressAlias());
            address.setStreetAddress2(silaAddress.getStreetAddress2());

            // Build the identity
            SilaIdentity silaIdentity = responseDTO.getIdentities().get(0);
            UserIdentity identity = new UserIdentity();
            identity.setIdentityValue(silaIdentity.getIdentityValue());
            identity.setIdentityAlias(silaIdentity.getIdentityAlias());
            identity.setUuid(silaIdentity.getUuid());
            identity.setAddedEpoch(silaIdentity.getAddedEpoch());
            identity.setModifiedEpoch(silaIdentity.getModifiedEpoch());

            // Build the kyc summary
            UserKycVerification kycVerification = kycVerificationRepository.findByUserEmail(email);
            UserKycSummary kycSummary = new UserKycSummary();
            kycSummary.setKycLevel(kycVerification.getKycLevel());
            kycSummary.setVendor(kycVerification.getVendor());
            kycSummary.setStatus(kycVerification.getStatus());
            kycSummary.setInternalReference(kycVerification.getInternalReference());
            kycSummary.setExternalReference(kycVerification.getExternalReference());
            kycSummary.setCreatedAt(kycVerification.getCreatedAt());
            kycSummary.setUpdatedAt(kycVerification.getUpdatedAt());

            // Build the admin, controlling officer and beneficial owner memberships if the user type is business
            if (user.getUserType().equalsIgnoreCase(UserType.BUSINESS.name())) {
                GmsSilaBusiness business = businessRepository.findByBusinessEmail(email);
                Long businessId = business.getId();
                List<GmsSilaBusinessOfficer> officers = businessOfficerRepository.findByBusinessIdAndStatus(businessId, LINKED.name());

                SilaMemberShipData beneficialOwner = responseDTO.getMembers().stream().filter(m -> m.getRole().equalsIgnoreCase(BENEFICIAL_OWNER.name())).findFirst().orElse(null);

                List<MemberShip> admins = officers.stream()
                        .filter(officer -> officer.getOfficerRole().equalsIgnoreCase(ADMINISTRATOR.name()))
                        .map(officer -> {
                            MemberShip memberShip = new MemberShip();
                            memberShip.setBusinessHandle(business.getBusinessHandle());
                            memberShip.setDetails("");
                            memberShip.setEntityName(officer.getOfficerHandle());
                            memberShip.setRole(ADMINISTRATOR.name());
                            memberShip.setOwnerShipStake(null);
                            memberShip.setCertificationToken(null);
                            return memberShip;
                        }).collect(Collectors.toList());

                List<MemberShip> controllingOfficers = officers.stream()
                        .filter(officer -> officer.getOfficerRole().equalsIgnoreCase(CONTROLLING_OFFICER.name()))
                        .map(officer -> {
                            MemberShip memberShip = new MemberShip();
                            memberShip.setBusinessHandle(business.getBusinessHandle());
                            memberShip.setDetails("");
                            memberShip.setEntityName(officer.getOfficerHandle());
                            memberShip.setRole(CONTROLLING_OFFICER.name());
                            memberShip.setOwnerShipStake(null);
                            memberShip.setCertificationToken(null);
                            return memberShip;
                        }).collect(Collectors.toList());

                List<MemberShip> beneficialOfficers = officers.stream()
                        .filter(officer -> officer.getOfficerRole().equalsIgnoreCase(BENEFICIAL_OWNER.name()))
                        .map(officer -> {
                            MemberShip memberShip = new MemberShip();
                            memberShip.setBusinessHandle(business.getBusinessHandle());
                            memberShip.setDetails(beneficialOwner == null ? "" : beneficialOwner.getDetails());
                            memberShip.setEntityName(officer.getOfficerHandle());
                            memberShip.setRole(BENEFICIAL_OWNER.name());
                            memberShip.setOwnerShipStake(beneficialOwner == null ? StringValues.EMPTY_STRING : beneficialOwner.getOwnerShipStake());
                            memberShip.setCertificationToken(beneficialOwner == null ? StringValues.EMPTY_STRING : beneficialOwner.getCertificationToken());
                            return memberShip;
                        }).collect(Collectors.toList());

                responsePayload.setBusinessAdmins(admins);
                responsePayload.setControllingOfficers(controllingOfficers);
                responsePayload.setBeneficialOwners(beneficialOfficers);
            }

            List<UserUploadDocument> userUploadDocument = uploadDocumentRepository.findByUserEmail(email);
            List<UserDocument> documents = userUploadDocument.stream()
                    .map(doc -> {
                        UserDocument document;
                        String json = JSON.toJson(doc);
                        document = JSON.fromJson(json, UserDocument.class);
                        return document;
                    }).collect(Collectors.toList());

            code = ResponseCode.SUCCESS;
            responsePayload.setEntity(userEntity);
            responsePayload.setContact(contact);
            responsePayload.setAddress(address);
            responsePayload.setIdentity(identity);
            responsePayload.setKycSummary(kycSummary);
            responsePayload.setDocuments(documents);
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
    }

    @Override
    public BaseResponse processBeneficialOwnerCertification(CertifyBeneficialOwnerRequestPayload requestPayload){
        String businessEmail = requestPayload.getBusinessEmail();
        String beneficialEmail = requestPayload.getOwnerEmail();
        SilaUser businessUser = silaUserRepository.findByGmsUserEmail(businessEmail);
        SilaUser beneficiaryUser = silaUserRepository.findByGmsUserEmail(beneficialEmail);

        BaseResponse response = new BaseResponse();
        String code;
        if(businessUser == null || businessUser.getSilaUserHandle().isBlank() || businessUser.getSilaUserHandle().isEmpty()){
            code = ResponseCode.USER_HANDLE_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code).concat(" for business handle"));
            return response;
        }

        if(beneficiaryUser == null || beneficiaryUser.getSilaUserHandle().isBlank() || beneficiaryUser.getSilaUserHandle().isEmpty()){
            code = ResponseCode.USER_HANDLE_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code).concat(" for beneficial owner handle"));
            return response;
        }

        GmsSilaBusiness business = businessRepository.findByBusinessEmail(businessEmail);
        GmsSilaBusinessOfficer admin = businessOfficerRepository.findByBusinessIdAndStatus(business.getId(), LINKED.name()).stream().filter(m -> m.getOfficerRole().equalsIgnoreCase("ADMINISTRATOR")).findFirst().orElse(null);
        if(admin == null){
            code = ResponseCode.NO_BUSINESS_ADMIN;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;
        }

        BaseResponse baseResponse = iIntegrationIdentityService.processBeneficialOwnerCertification(beneficiaryUser.getSilaUserHandle(), admin.getOfficerHandle(), business.getBusinessHandle(), requestPayload.getCertificationToken());
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            GmsSilaBusinessOfficer benOwnerOfficer = businessOfficerRepository.findByEmailAndOfficerRoleAndStatus(beneficialEmail, BENEFICIAL_OWNER.name(), LINKED.name()).stream().filter(officer -> officer.getBusinessId() == business.getId()).findFirst().orElse(null);
            if(benOwnerOfficer != null) {
                benOwnerOfficer.setCertified(true);
                benOwnerOfficer.setCertifiedAt(LocalDateTime.now().toString());
                businessOfficerRepository.saveAndFlush(benOwnerOfficer);
            }
        }

        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessCertification(String businessEmail){
        GmsSilaBusiness business = businessRepository.findByBusinessEmail(businessEmail);
        BaseResponse response = new BaseResponse();
        String code;

        if(business == null){
            code = ResponseCode.RECORD_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code).concat(" for business handle"));
            return response;
        }

        GmsSilaBusinessOfficer admin = businessOfficerRepository.findByBusinessIdAndStatus(business.getId(), LINKED.name()).stream().filter(m -> m.getOfficerRole().equalsIgnoreCase(ADMINISTRATOR.name())).findFirst().orElse(null);
        if(admin == null){
            code = ResponseCode.NO_BUSINESS_ADMIN;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;
        }

        BaseResponse baseResponse = iIntegrationIdentityService.processBusinessCertification(admin.getOfficerHandle(), business.getBusinessHandle());
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)) {
            business.setCertified(true);
            business.setCertifiedAt(LocalDateTime.now().toString());
        }

        business.setRequiresCertification(true);
        businessRepository.saveAndFlush(business);

        return baseResponse;
    }

    @Override
    public BaseResponse processIndividualKycDocumentUpload(String authToken, IndividualDocumentUploadRequestPayload requestPayload){
        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));
        GmsUser user = iUserRepository.findByEmailAddress(email);

        // Create the file from the base64 String
        String base64ImageString = requestPayload.getFileContent();
        byte[] bytes = DatatypeConverter.parseBase64Binary(base64ImageString);
        String fileName = String.join("-", String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)), requestPayload.getFileName()).concat(".jpg");
        String filePath = Paths.get("src", "main", "resources", fileName).toFile().getAbsolutePath();

        File file = new File(filePath);

        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))){
            outputStream.write(bytes);
        }catch (Exception e){
            e.printStackTrace();
            log.info(e.getMessage());
            log.info("Error while trying to write bytes to file for upload: {}", e.getMessage());
        }

        // Create the local document in the system first
        UserUploadDocument document = uploadDocumentRepository.findByUserEmailAndDocumentType(email, requestPayload.getDocumentType());
        if(document == null){
            document = new UserUploadDocument();
        }
        document.setUserEmail(email);
        document.setName(requestPayload.getName());
        document.setMimeType(requestPayload.getMimeType());
        document.setFileName(requestPayload.getFileName());
        document.setFileExtension(requestPayload.getFileExtension());
        document.setFileSize(requestPayload.getFileSize());
        document.setFileDescription(requestPayload.getFileDescription());
        document.setDocumentType(requestPayload.getDocumentType());
        document.setFileType(requestPayload.getFileType());
        document.setCreatedAt(LocalDateTime.now().toString());
        document.setUpdatedAt(LocalDateTime.now().toString());
        document.setExternalReference(requestPayload.getExternalReference());
        document.setFileContent(requestPayload.getFileContent());
        document.setIdentityType(requestPayload.getIdentityType());
        document.setFileBinaryData(genericService.getBinaryDataFromFile(file));
        document.setHash(genericService.hashFileContent(file, HashAlgo.SHA_256));
        document.setAbsolutePath(file.getAbsolutePath());

        UserUploadDocument locallyCreatedDocument = uploadDocumentRepository.saveAndFlush(document);

        // Now upload the document to sila
        return iIntegrationIdentityService.processSingleKycDocumentUpload(document);
    }

    @Override
    public BaseResponse processBusinessTypes(){
        return iIntegrationIdentityService.processBusinessTypesRequest();
    }

    @Override
    public BaseResponse processBusinessCategories() {
        return iIntegrationIdentityService.processBusinessCategoriesRequest();
    }

    @Override
    public BaseResponse processDocumentTypes(){
        return iIntegrationIdentityService.processDocumentTypesRequest();
    }

    @Override
    public BaseResponse processBusinessRoleRequest(){
        return iIntegrationIdentityService.processBusinessRolesRequest();
    }

    @Override
    public BaseResponse processChangePinRequest(String authToken, TransactionPinChangeRequestPayload requestPayload) {
        String code = ResponseCode.SYSTEM_ERROR;
        BaseResponse response = new BaseResponse();
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));

        String userEmail = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));
        GmsUser user = iUserRepository.findByEmailAddress(userEmail);
        validator.validateUserExistValidationThrowException(userEmail);

        // Check if the deviceId match on same devices.
        if(requestPayload.getChannel().equalsIgnoreCase(RequestChannel.MOBILE.name())){
            if(requestPayload.getDeviceId() == null || requestPayload.getDeviceId().isBlank() || requestPayload.getDeviceId().isEmpty()){
                code = ResponseCode.INVALID_DEVICE_ID;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                return response;
            }

            if(!user.getDeviceId().equalsIgnoreCase(requestPayload.getDeviceId())){
                code = ResponseCode.INVALID_DEVICE_ID;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                return response;
            }
        }

        // Check if the old transaction matches.
        String savedOldPinHash = user.getTransactionPin();
        if(!passwordUtil.isPasswordMatch(requestPayload.getOldPin(), savedOldPinHash)){
            code = ResponseCode.INCORRECT_TRANSACTION_PIN;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;
        }

        // Update the transaction pin of the user.
        user.setTransactionPin(passwordUtil.hashPassword(requestPayload.getNewPin()));
        user.setPinUpdatedBy(Creator.USER.name());
        user.setPinUpdatedAt(LocalDateTime.now().toString());
        iUserRepository.saveAndFlush(user);

        // Return success message to the client application
        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        return response;
    }

    @Override
    public BaseResponse processForgetPinOtpRequest(String authToken, String deviceId) {
        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));
        GmsUser user = iUserRepository.findByEmailAddress(email);
        OtpSendInfo otpSendInfo = otpUtil.sendPinChangeOtpRequest(email, deviceId);

        BaseResponse response = new BaseResponse();
        response.setResponseCode(ResponseCode.SUCCESS);
        response.setResponseMessage(messageProvider.getMessage(response.getResponseCode()));
        return response;
    }

    @Override
    public BaseResponse processForgetPinOtpVerification(String authToken, TransactionPinResetOtpVerificationRequestPayload requestPayload) {
        BaseResponse response = new BaseResponse();
        String code = ResponseCode.SYSTEM_ERROR;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));

        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));
        GmsUser user = iUserRepository.findByEmailAddress(email);
        validator.validateUserExistValidationThrowException(email);

        // Check if the deviceId match on same devices.
        if(requestPayload.getChannel().equalsIgnoreCase(RequestChannel.MOBILE.name())){
            if(requestPayload.getDeviceId() == null || requestPayload.getDeviceId().isBlank() || requestPayload.getDeviceId().isEmpty()){
                code = ResponseCode.INVALID_DEVICE_ID;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                return response;
            }

            if(!user.getDeviceId().equalsIgnoreCase(requestPayload.getDeviceId())){
                code = ResponseCode.INVALID_DEVICE_ID;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                return response;
            }

        }

        // Check for the correctness of the otp
        GmsUserOtp userOtp = otpRepository.findByOtpTypeAndOtpOwner("PIN_CHANGE", email);
        if(userOtp == null){
            code = ResponseCode.OTP_RECORD_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;
        }

        if(!userOtp.getDeviceId().equalsIgnoreCase(requestPayload.getDeviceId())){
            code = ResponseCode.INVALID_DEVICE_ID;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;
        }

        String otpHashStored = userOtp.getOtpValue();
        if(!passwordUtil.isPasswordMatch(requestPayload.getOtp(), otpHashStored)){
            code = ResponseCode.OTP_INCORRECT;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;
        }

        // Update the otp entry
        userOtp.setVerified(true);
        userOtp.setUpdatedAt(LocalDateTime.now().toString());
        otpRepository.saveAndFlush(userOtp);

        // Change the pin of the user
        user.setTransactionPin(passwordUtil.hashPassword(requestPayload.getNewPin()));
        user.setPinUpdatedAt(LocalDateTime.now().toString());
        user.setPinUpdatedBy(Creator.USER.name());
        iUserRepository.saveAndFlush(user);

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        return response;
    }

    private String cleanToken(String authToken){
        return authToken.startsWith(StringValues.AUTH_HEADER_BEARER_KEY) ? authToken.replace(StringValues.AUTH_HEADER_BEARER_KEY, StringValues.EMPTY_STRING).trim() : authToken.trim();
    }
}
                                                                                                                                                