package com.gms.alquimiapay.modules.user.service.impl;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.*;
import com.gms.alquimiapay.modules.account.model.VirtualAccountCache;
import com.gms.alquimiapay.modules.account.payload.data.WireVirtualAccountData;
import com.gms.alquimiapay.modules.account.repository.IAccountRepository;
import com.gms.alquimiapay.modules.account.service.IAccountService;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.kyc.constant.KycStatus;
import com.gms.alquimiapay.modules.kyc.model.UserKycVerification;
import com.gms.alquimiapay.modules.kyc.repository.UserKycVerificationRepository;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.model.GmsUserDevice;
import com.gms.alquimiapay.modules.user.model.GmsUserOtp;
import com.gms.alquimiapay.modules.user.model.UserRole;
import com.gms.alquimiapay.modules.user.payload.onboarding.data.*;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.ForgotPasswordOtpVerifyRequestPayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.SignupOtpVerificationRequestPayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.UserLoginRequestPayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.UserSignupRequestPayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.response.SignupOtpVerificationResponsePayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.response.UserLoginResponsePayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.response.UserSignupResponsePayload;
import com.gms.alquimiapay.modules.user.repository.IUserDeviceRepository;
import com.gms.alquimiapay.modules.user.repository.IUserOtpRepository;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.modules.user.repository.IUserRoleRepository;
import com.gms.alquimiapay.modules.user.service.IUserOnboardingService;
import com.gms.alquimiapay.modules.user.validation.IUserServiceValidator;
import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletData;
import com.gms.alquimiapay.modules.wallet.payload.response.CreateWalletResponsePayload;
import com.gms.alquimiapay.modules.wallet.repository.IGmsWalletCacheRepository;
import com.gms.alquimiapay.modules.wallet.service.IWalletService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.payload.ErrorResponse;
import com.gms.alquimiapay.payload.OtpSendInfo;
import com.gms.alquimiapay.payload.ValidationPayload;
import com.gms.alquimiapay.util.JwtUtil;
import com.gms.alquimiapay.util.OtpUtil;
import com.gms.alquimiapay.util.PasswordUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserOnboardingService implements IUserOnboardingService {
    private final Environment env;
    private final UserKycVerificationRepository userKycVerificationRepository;
    private final MessageProvider messageProvider;
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final OtpUtil otpUtil;
    private final IUserServiceValidator userServiceValidator;

    private final IUserDeviceRepository userDeviceRepository;
    private final IUserOtpRepository otpRepository;
    private final IWalletService walletService;

    private final IAccountService accountService;

    private final IGmsWalletCacheRepository walletCacheRepository;
    private final IAccountRepository accountRepository;

    private static final Gson JSON = new Gson();

    @Autowired
    public UserOnboardingService(
            Environment env, UserKycVerificationRepository userKycVerificationRepository,
            MessageProvider messageProvider,
            IUserRepository userRepository,
            IUserRoleRepository userRoleRepository,
            PasswordUtil passwordUtil,
            JwtUtil jwtUtil, OtpUtil otpUtil,
            IUserServiceValidator userServiceValidator,
            IUserDeviceRepository userDeviceRepository,
            IUserOtpRepository otpRepository,
            @Qualifier(QualifierService.LOCAL_WALLET_SERVICE)
            IWalletService walletService,
            @Qualifier(QualifierService.CIRCLE_ACCOUNT_SERVICE)
            IAccountService accountService,
            IGmsWalletCacheRepository walletCacheRepository,
            IAccountRepository accountRepository) {
        this.env = env;
        this.userKycVerificationRepository = userKycVerificationRepository;
        this.messageProvider = messageProvider;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
        this.otpUtil = otpUtil;
        this.userServiceValidator = userServiceValidator;
        this.userDeviceRepository = userDeviceRepository;
        this.otpRepository = otpRepository;
        this.walletService = walletService;
        this.accountService = accountService;
        this.walletCacheRepository = walletCacheRepository;
        this.accountRepository = accountRepository;
    }


    @Override
    public String processSignupUser(UserSignupRequestPayload requestPayload) {

        UserCredentials userCredentials = UserCredentials.getInstance();
        userCredentials.setUsername(requestPayload.getUsername());
        userCredentials.setEmailAddress(requestPayload.getEmailAddress());
        userCredentials.setMobileNumber(requestPayload.getPhoneNumber());

        ValidationPayload validationPayload;
        validationPayload = userServiceValidator.processUserExistsValidation(userCredentials);
        if(validationPayload.isHasError()){
            return validationPayload.getErrorJson();
        }

        validationPayload = userServiceValidator.processChannelValidation(requestPayload.getChannel(), requestPayload.getDeviceId());
        if(validationPayload.isHasError()){
            return validationPayload.getErrorJson();
        }

        validationPayload = userServiceValidator.processUserTypeValidation(requestPayload.getUserType(), requestPayload.getBusinessName());
        if(validationPayload.isHasError()){
            return validationPayload.getErrorJson();
        }

        GmsUser newUser = this.createNewUserEntity(requestPayload);
        GmsUser createdUser = userRepository.saveAndFlush(newUser);

        // Send OTP to the user email asynchronously.
        CompletableFuture
                .runAsync(() -> {
                    OtpSendInfo otpSendInfo = otpUtil.sendSignUpOtpToMail(requestPayload.getEmailAddress());
                    createdUser.setOtp(passwordUtil.hashPassword(otpSendInfo.getOtpSent()));
                    createdUser.setOtpCreatedDate(otpSendInfo.getCreatedDateTime().toString());
                    createdUser.setOtpExpDate(otpSendInfo.getExpirationDateTime().toString());
                    userRepository.save(createdUser);
                });

        try {
            //Create wallet and virtual account for customer
            CreateWalletResponsePayload walletResponsePayload = walletService.processNewWalletCreation(createdUser.getAuthToken(), null);
            log.info("Wallet creation response: {}", JSON.toJson(walletResponsePayload));
        }catch (Exception e){
            log.error("Error while trying to create customer wallet: {}", e.getMessage());
            createdUser.setWalletCreationFailureReason(e.getMessage());
            userRepository.saveAndFlush(createdUser);
        }

        try{
            BaseResponse accountResponse = accountService.processVirtualAccountCreation(createdUser.getEmailAddress());
            log.info("Account creation response: {}", JSON.toJson(accountResponse));
        }catch (Exception e){
            log.error("Error while trying to create customer wire virtual account: {}", e.getMessage());
            createdUser.setVirtualAccountCreationFailureReason(e.getMessage());
            userRepository.saveAndFlush(createdUser);
        }

        // Send success response to client application.
        UserSignupResponseData responseData = getUserSignupResponseData(newUser);

        UserSignupResponsePayload responsePayload = UserSignupResponsePayload.createInstance();
        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setResponseMessage(messageProvider.getMessage(responsePayload.getResponseCode()));
        responsePayload.setResponseData(responseData);

        return JSON.toJson(responsePayload);
    }

    private static UserSignupResponseData getUserSignupResponseData(GmsUser newUser) {
        UserSignupResponseData responseData = UserSignupResponseData.createInstance();
        responseData.setAuthToken(newUser.getAuthToken());
        responseData.setStatus(newUser.getStatus());
        responseData.setCreatedAt(newUser.getCreatedAt());
        responseData.setUpdatedAt(newUser.getUpdatedAt());
        responseData.setUsername(newUser.getUsername());
        responseData.setEmailAddress(newUser.getEmailAddress());
        responseData.setVerified(false);
        responseData.setRequiresVerification(true);
        return responseData;
    }


    @Override
    public String processSignupOtpVerification(SignupOtpVerificationRequestPayload requestPayload) {
        ErrorResponse errorResponse = ErrorResponse.getInstance();
        String otpFromUser = requestPayload.getOtp();
        String userEmail = requestPayload.getEmailAddress();

        // Check for the existence of the user in the system.
        GmsUser user = userRepository.findByEmailAddress(userEmail);
        if(user == null) {
           errorResponse.setResponseCode(ResponseCode.RECORD_NOT_FOUND);
           errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
           return JSON.toJson(errorResponse);
        }

        // Check if Otp is already verified.
        if(user.getIsOtpVerified()){
            errorResponse.setResponseCode(ResponseCode.OTP_ALREADY_VERIFIED);
            errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
            return JSON.toJson(errorResponse);
        }

        // Check if otp has expired.
        if(LocalDateTime.parse(user.getOtpExpDate()).isBefore(LocalDateTime.now())){
           errorResponse.setResponseCode(ResponseCode.OTP_EXPIRED);
           errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
           return JSON.toJson(errorResponse);
        }

        // Check for the correctness of the otp.
        boolean isOtpCorrect = passwordUtil.isPasswordMatch(otpFromUser, user.getOtp());
        if(!isOtpCorrect){
           errorResponse.setResponseCode(ResponseCode.OTP_INCORRECT);
           errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
           return JSON.toJson(errorResponse);
        }

        // Update the user status.
        user.setStatus(UserStatus.ACTIVE.name());
        user.setIsOtpVerified(true);
        user.setIsVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now().toString());
        userRepository.saveAndFlush(user);

        // Return response to application client.
        SignupVerificationResponseData responseData = SignupVerificationResponseData.createInstance();
        responseData.setUserStatus(UserStatus.ACTIVE.name());
        responseData.setEmailVerified(true);
        responseData.setVerifiedAt(LocalDateTime.now().toString());
        responseData.setEmailToVerify(userEmail);

        SignupOtpVerificationResponsePayload responsePayload = SignupOtpVerificationResponsePayload.createInstance();
        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setResponseMessage(messageProvider.getMessage(responsePayload.getResponseCode()));
        responsePayload.setResponseData(responseData);
        return JSON.toJson(responsePayload);
    }

    @Override
    public String processUserLogin(UserLoginRequestPayload requestPayload) {
        ErrorResponse errorResponse = ErrorResponse.getInstance();
        String userEmail = requestPayload.getEmailAddress().trim();

        // Check if user exists
        ValidationPayload validationPayload = userServiceValidator.processUserExistsByEmailValidation(requestPayload.getEmailAddress());
        if(validationPayload.isHasError()) {
            return validationPayload.getErrorJson();
        }

        GmsUser user = validationPayload.getExtraItem();

        // Check if user is locked.
        if(user.getStatus().equalsIgnoreCase(UserStatus.LOCKED.name())){
            errorResponse.setResponseCode(ResponseCode.ACCOUNT_LOCKED);
            errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
            return JSON.toJson(errorResponse);
        }

        // Check if the account is not yet verified
        if(user.getStatus().equalsIgnoreCase(UserStatus.UNVERIFIED.name())){
            errorResponse.setResponseCode(ResponseCode.ACCOUNT_UNVERIFIED);
            errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
            return JSON.toJson(errorResponse);
        }

        // Check for the correctness of the password.
        boolean isPasswordMatch = passwordUtil.isPasswordMatch(requestPayload.getPassword(), user.getPassword());
        if(!isPasswordMatch){
            int newLoginCount = user.getLoginAttempt() + 1;
            user.setLoginAttempt(newLoginCount);
            if(newLoginCount >= 5){
                user.setStatus(UserStatus.LOCKED.name());
            }
            userRepository.save(user);
            errorResponse.setResponseCode(ResponseCode.INVALID_PASSWORD);
            errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
            return JSON.toJson(errorResponse);
        }

        // Check that the user logs in with the same device as device of signup.
        if(!user.getDeviceId().equalsIgnoreCase(requestPayload.getDeviceId())){
            // Fetch all deviceIds of the user and see if it matches, then update accordingly.
            List<GmsUserDevice> devices = userDeviceRepository.findByOwnerEmail(user.getEmailAddress());
            List<String> deviceIds = devices.stream().map(GmsUserDevice::getDeviceId).collect(Collectors.toList());
            if(!deviceIds.contains(requestPayload.getDeviceId())){
                log.info("New device found: {}", requestPayload.getDeviceId());
                errorResponse.setResponseCode(ResponseCode.DEVICE_ID_MIS_MATCH);
                errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));

                // Asynchronously send verification otp email to the user email address.
                log.info("Sending new device linking otp to user's email");
                this.processNewDeviceLinkOtpRequest(user.getEmailAddress(), requestPayload.getDeviceId());
                return JSON.toJson(errorResponse);
            }

            // It is possible that otp email has been sent, but user refuses to link the device.
            GmsUserDevice possibleDevice = userDeviceRepository.findByDeviceIdAndOwnerEmail(requestPayload.getDeviceId(), user.getEmailAddress());
            if(!possibleDevice.isLinked()){
                // It is possible that the otp initially sent has expired.
                GmsUserOtp otp = otpRepository.findByOtpTypeAndOtpOwner("DEVICE_LINK", user.getEmailAddress());
                if(LocalDateTime.parse(otp.getExpAt()).isBefore(LocalDateTime.now())){
                    this.processNewDeviceLinkOtpRequest(user.getEmailAddress(), requestPayload.getDeviceId()); // Resend the otp email automatically.
                }
                errorResponse.setResponseCode(ResponseCode.DEVICE_NOT_YET_LINKED);
                errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
                return JSON.toJson(errorResponse);
            }

            // The user is using another linked device to login. Update the user's current deviceId
            user.setDeviceId(requestPayload.getDeviceId());
            userRepository.saveAndFlush(user);
        }

        // Update user login credentials.
        user.setLastLoginDate(LocalDateTime.now().toString());
        user.setAuthToken(jwtUtil.createJWTString(user.getEmailAddress()));
        user.setAuthTokenCreatedDate(LocalDateTime.now().toString());
        user.setAuthTokenExpirationDate(jwtUtil.getJWTExpiration(LocalDateTime.now()).toString());
        user.setStatus(UserStatus.LOGGED_IN.name());
        userRepository.save(user);

        // Get KYC data.
        UserKycVerification kycVerification = userKycVerificationRepository.findByUserEmail(user.getEmailAddress());
        UserKycData kycData = new UserKycData();
        kycData.setKycLevel(kycVerification == null || kycVerification.getKycLevel() == null || kycVerification.getKycLevel().isEmpty() ? "DOC_KYC" : kycVerification.getKycLevel());
        kycData.setStatus(kycVerification == null || kycVerification.getStatus() == null || kycVerification.getStatus().isEmpty() ? KycStatus.PENDING.name() : kycVerification.getStatus());
        kycData.setKycTier(kycVerification == null || kycVerification.getKycTier() == null || kycVerification.getKycTier().isEmpty() ? "1" : kycVerification.getKycTier());

        // Get the user wallet
        GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(userEmail);
        WalletData walletData = null;
        if(walletCache != null) {
            walletData = new WalletData();
            walletData.setStatus(walletCache.getStatus());
            walletData.setBlockchainNetwork(walletCache.getBlockchainNetwork());
            walletData.setBlockchainAddress(walletCache.getBlockchainAddress());
            walletData.setWalletName(walletCache.getWalletName());
            walletData.setWalletId(walletCache.getWalletId());
            walletData.setAvailableBalance(walletCache.getAvailableBalance());
            walletData.setPendingBalance(walletCache.getPendingBalance());
            walletData.setIsWhiteListed(walletCache.getIsWhiteListed());
            walletData.setOwnerFullName(walletCache.getOwnerFullName());
            walletData.setIsDefault(true);
        }

        // Get the user associated virtual account.
        VirtualAccountCache virtualAccountCache = accountRepository.findByInternalCustomerEmail(userEmail);
        WireVirtualAccountData accountData = null;
        if(virtualAccountCache != null) {
            accountData = new WireVirtualAccountData();
            accountData.setAccountNumber(virtualAccountCache.getAccountNumber());
            accountData.setRoutingNumber(virtualAccountCache.getRoutingNumber());
            accountData.setCustomerName(virtualAccountCache.getInternalCustomerName());
            accountData.setBillingDetails(virtualAccountCache.getAccountBillingDetails());
            accountData.setBankAddress(virtualAccountCache.getBankAddress());
            accountData.setStatus(virtualAccountCache.getStatus().equalsIgnoreCase(ModelStatus.COMPLETE.name()) ? ModelStatus.ACTIVE.name() : virtualAccountCache.getStatus());
        }

        // Return response to client application.
        UserLoginResponseData responseData = UserLoginResponseData.createInstance();
        responseData.setAddress(user.getAddress());
        responseData.setCity(user.getCity());
        responseData.setUserId(user.getUserId());
        responseData.setCountry(user.getCountry());
        responseData.setStatus(user.getStatus());
        responseData.setDeviceId(user.getDeviceId());
        responseData.setUsername(user.getUsername());
        responseData.setEmailAddress(user.getEmailAddress());
        responseData.setUserType(user.getUserType());
        responseData.setBusinessName(user.getBusinessName());
        responseData.setAuthToken(user.getAuthToken());
        responseData.setCreatedDate(user.getCreatedAt());
        responseData.setFirstName(user.getFirstName());
        responseData.setLastName(user.getLastName());
        responseData.setFullName(String.join(StringValues.SINGLE_EMPTY_SPACE, user.getLastName(), user.getFirstName()));
        responseData.setGeoLocation(user.getGeoLocation());
        responseData.setMiddleName(user.getMiddleName());
        responseData.setPhotoLink(user.getPhotoLink());
        responseData.setModifiedDate(LocalDateTime.now().toString());
        responseData.setMobileNumber(user.getMobileNumber());
        responseData.setLanguage(user.getLocale());
        responseData.setLastLoginDate(user.getLastLoginDate() == null ? LocalDateTime.now().toString() : user.getLastLoginDate());
        responseData.setKyc(kycData);
        responseData.setWallet(walletData);
        responseData.setAccount(accountData);

        UserLoginResponsePayload responsePayload = UserLoginResponsePayload.createInstance();
        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setResponseMessage(messageProvider.getMessage(responsePayload.getResponseCode()));
        responsePayload.setResponseData(responseData);
        return JSON.toJson(responsePayload);
    }

    @Override
    public String processUserLogout(String authToken){
        authToken = cleanToken(authToken);
        ErrorResponse errorResponse = ErrorResponse.getInstance();

        // Check if the user exist
        String email = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));
        ValidationPayload validationPayload = userServiceValidator.processUserExistsByEmailValidation(email);
        if(validationPayload.isHasError()){
            return validationPayload.getErrorJson();
        }

        GmsUser user = validationPayload.getExtraItem();

        // Check if user is already logged out.
        user.setStatus(UserStatus.LOGGED_OUT.name());
        user.setLastLoginDate(LocalDateTime.now().toString());
        user.setAuthTokenExpirationDate(LocalDateTime.now().toString());
        userRepository.save(user);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setResponseCode(ResponseCode.SUCCESS);
        baseResponse.setResponseMessage(messageProvider.getMessage(baseResponse.getResponseCode()));
        return JSON.toJson(baseResponse);
    }

    @Override
    public String processForgotPasswordOtpRequest(String email) {
        BaseResponse baseResponse = new BaseResponse();

        CompletableFuture.runAsync(() -> {
            OtpSendInfo otpSendInfo = otpUtil.sendForgetPasswordOtpToMail(email);
        });

        baseResponse.setResponseCode(ResponseCode.SUCCESS);
        baseResponse.setResponseMessage(messageProvider.getMessage(ResponseCode.SUCCESS));
        return JSON.toJson(baseResponse);
    }

    @Override
    public String processForgotPasswordOtpVerification(String email, ForgotPasswordOtpVerifyRequestPayload requestPayload) {

        String otp = requestPayload.getOtp();
        BaseResponse response = new BaseResponse();
        String code;

        // Check that the user has at least one otp record.
        GmsUserOtp userOtp = otpRepository.findByOtpTypeAndOtpOwner("FORGOT_PASSWORD", email);
        if(userOtp == null){
            code = ResponseCode.OTP_RECORD_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Check that the otp has not already been verified.
        if(userOtp.isVerified()){
            code = ResponseCode.OTP_ALREADY_VERIFIED;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Check that the otp has not expired.
        if(LocalDateTime.parse(userOtp.getExpAt()).isBefore(LocalDateTime.now())){
            code = ResponseCode.OTP_EXPIRED;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Check for the correctness of the otp.
        if(!passwordUtil.isPasswordMatch(otp, userOtp.getOtpValue())){
            code = ResponseCode.OTP_INCORRECT;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Update the otp record.
        userOtp.setUpdatedAt(LocalDateTime.now().toString());
        userOtp.setVerified(true);
        otpRepository.saveAndFlush(userOtp);

        // Reset the password.
        this.processResetPassword(email, requestPayload.getNewPassword(), requestPayload.getDeviceId());

        // Send response to client.
        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));

        return JSON.toJson(response);
    }

    @Override
    public String processResetPassword(String email, String newPassword, String deviceId) {
        GmsUser user = userRepository.findByEmailAddress(email);

        BaseResponse response = new BaseResponse();
        String code;

        // TODO: Perform extra security checks.
        if(!user.getDeviceId().equalsIgnoreCase(deviceId)){
            code = ResponseCode.INVALID_DEVICE_ID;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Update the user's password.
        user.setPassword(passwordUtil.hashPassword(newPassword));
        userRepository.saveAndFlush(user);

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        return JSON.toJson(response);
    }

    @Override
    public String processNewDeviceLinkOtpRequest(String email, String newDeviceId) {
        BaseResponse baseResponse = new BaseResponse();

        String code;

        // Ensure that the user exist
        GmsUser user = userRepository.findByEmailAddress(email);
        if(user == null){
            code = ResponseCode.RECORD_NOT_FOUND;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(baseResponse);
        }

        // Ensure that the device is not already linked.
        GmsUserDevice userDevice = userDeviceRepository.findByDeviceIdAndOwnerEmail(newDeviceId, email);
        if(userDevice != null && userDevice.isLinked()){
            code = ResponseCode.DEVICE_ALREADY_LINKED;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(baseResponse);
        }

        CompletableFuture.runAsync(() -> {
            OtpSendInfo otpSendInfo = otpUtil.sendNewDeviceLinkOtp(email, newDeviceId);
        });

        baseResponse.setResponseCode(ResponseCode.SUCCESS);
        baseResponse.setResponseMessage(messageProvider.getMessage(ResponseCode.SUCCESS));
        return JSON.toJson(baseResponse);
    }

    @Override
    public String processNewDeviceLinkOtpVerification(String email, String newDeviceId, String otp) {

        BaseResponse response = new BaseResponse();

        String code;

        // Ensure that the user exist
        GmsUser user = userRepository.findByEmailAddress(email);
        if(user == null){
            code = ResponseCode.RECORD_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Check that the user has at least one otp record.
        GmsUserOtp userOtp = otpRepository.findByOtpTypeAndOtpOwner("DEVICE_LINK", email);
        if(userOtp == null){
            code = ResponseCode.OTP_RECORD_NOT_FOUND;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Check that the otp has not already been verified.
        if(userOtp.isVerified()){
            code = ResponseCode.OTP_ALREADY_VERIFIED;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Check that the otp has not expired.
        if(LocalDateTime.parse(userOtp.getExpAt()).isBefore(LocalDateTime.now())){
            code = ResponseCode.OTP_EXPIRED;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));

            // Automatically send another otp.
            this.processNewDeviceLinkOtpRequest(email, newDeviceId);
            return JSON.toJson(response);
        }

        // Check for the correctness of the otp.
        if(!passwordUtil.isPasswordMatch(otp, userOtp.getOtpValue())){
            code = ResponseCode.OTP_INCORRECT;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Check that the device was saved initially at otp request at user request.
        GmsUserDevice device = userDeviceRepository.findByDeviceIdAndOwnerEmail(newDeviceId, email);
        if(device == null){
            code = ResponseCode.DEVICE_NOT_INITIATED_FOR_LINKING;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Check that the device used to request for otp is same as the one requesting verification.
        if(!userOtp.getDeviceId().equalsIgnoreCase(newDeviceId)){
            code = ResponseCode.DEVICE_MIS_MATCH;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return JSON.toJson(response);
        }

        // Update the otp record and the device record.
        device.setLinked(true);
        device.setUpdatedAt(LocalDateTime.now().toString());
        userDeviceRepository.saveAndFlush(device);

        userOtp.setUpdatedAt(LocalDateTime.now().toString());
        userOtp.setVerified(true);
        otpRepository.saveAndFlush(userOtp);

        // Update the user's current device to this one.
        user.setDeviceId(newDeviceId);
        userRepository.saveAndFlush(user);

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        return JSON.toJson(response);

    }

    private GmsUser createNewUserEntity(UserSignupRequestPayload requestPayload){

        // Create a new user entity.
        GmsUser newUser = new GmsUser();
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setFirstName(requestPayload.getFirstName());
        newUser.setMiddleName(StringValues.EMPTY_STRING);
        newUser.setLastName(requestPayload.getLastName());
        newUser.setEmailAddress(requestPayload.getEmailAddress());
        newUser.setPassword(passwordUtil.hashPassword(requestPayload.getPassword()));
        newUser.setMobileNumber(requestPayload.getPhoneNumber());
        newUser.setLastLoginDate(null);
        newUser.setAuthToken(jwtUtil.createJWTString(requestPayload.getEmailAddress()));
        newUser.setCreatedAt(LocalDateTime.now().toString());
        newUser.setUpdatedAt(LocalDateTime.now().toString());
        newUser.setVerifySubmitStatus("");
        newUser.setIsVerified(false);
        newUser.setBusinessName(requestPayload.getBusinessName());
        newUser.setLocale(Locale.ENGLISH.toString());
        UserRole userRole = userRoleRepository.findByRoleName(RoleName.USER.name());
        if(userRole == null)
            userRole = userRoleRepository.getDefaultOrdinaryUserRole();
        newUser.setUserRoleId(userRole.getId());  // DEFAULT USER
        newUser.setUsername(requestPayload.getUsername());
        newUser.setPhotoLink(Objects.requireNonNull(env.getProperty("gms.base-url")).concat("/man.jpg"));
        newUser.setCreatedBy(Creator.SYSTEM.name());
        newUser.setModifiedBy(Creator.SYSTEM.name());
        newUser.setDeviceId(requestPayload.getChannel().toUpperCase().equals(RequestChannel.MOBILE.name()) ? requestPayload.getDeviceId() : null);
        newUser.setGeoLocation(StringValues.EMPTY_STRING);
        newUser.setLoginAttempt(0);
        newUser.setGender(StringValues.EMPTY_STRING);
        newUser.setChannel(requestPayload.getChannel());
        newUser.setAuthTokenCreatedDate(LocalDateTime.now().toString());
        newUser.setAuthTokenExpirationDate(jwtUtil.getJWTExpiration(LocalDateTime.now()).toString());
        newUser.setIsOtpVerified(false);
        newUser.setStatus(UserStatus.UNVERIFIED.name());
        newUser.setUserType(requestPayload.getUserType().toUpperCase());
        newUser.setTransactionPin(passwordUtil.hashPassword(requestPayload.getTransactionPin()));
        newUser.setPinCreatedAt(LocalDateTime.now().toString());
        newUser.setCreatedBy(Creator.SYSTEM.name());
        newUser.setUpdatedAt(LocalDateTime.now().toString());
        newUser.setPinUpdatedBy(Creator.SYSTEM.name());

        return newUser;
    }

    private String cleanToken(String authToken){
        return authToken.startsWith(StringValues.AUTH_HEADER_BEARER_KEY) ? authToken.replace(StringValues.AUTH_HEADER_BEARER_KEY, StringValues.EMPTY_STRING).trim() : authToken.trim();
    }
}
