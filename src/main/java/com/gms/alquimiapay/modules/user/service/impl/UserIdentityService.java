package com.gms.alquimiapay.modules.user.service.impl;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.Creator;
import com.gms.alquimiapay.constants.RequestChannel;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.generic.service.IGenericService;
import com.gms.alquimiapay.modules.kyc.repository.UserKycVerificationRepository;
import com.gms.alquimiapay.modules.kyc.repository.UserUploadDocumentRepository;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.model.GmsUserOtp;
import com.gms.alquimiapay.modules.user.payload.identity.request.TransactionPinChangeRequestPayload;
import com.gms.alquimiapay.modules.user.payload.identity.request.TransactionPinResetOtpVerificationRequestPayload;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserIdentityService implements IUserIdentityService
{

    private final IUserRepository iUserRepository;
    private final MessageProvider messageProvider;
    private final JwtUtil jwtUtil;
    private final EmailMessenger emailMessenger;
    private final IGenericService genericService;
    private final UserUploadDocumentRepository uploadDocumentRepository;
    private final UserKycVerificationRepository kycVerificationRepository;
    private final UserServiceValidator validator;
    private final PasswordUtil passwordUtil;
    private final OtpUtil otpUtil;
    private final IUserOtpRepository otpRepository;

    private static final Gson JSON = new Gson();

    @Autowired
    public UserIdentityService(
            IUserRepository iUserRepository,
            MessageProvider messageProvider,
            JwtUtil jwtUtil,
            EmailMessenger emailMessenger,
            @Lazy IGenericService genericService,
            UserUploadDocumentRepository uploadDocumentRepository,
            UserKycVerificationRepository kycVerificationRepository,
            UserServiceValidator validator,
            PasswordUtil passwordUtil,
            OtpUtil otpUtil,
            IUserOtpRepository otpRepository) {
        this.iUserRepository = iUserRepository;
        this.messageProvider = messageProvider;
        this.jwtUtil = jwtUtil;
        this.emailMessenger = emailMessenger;
        this.genericService = genericService;
        this.uploadDocumentRepository = uploadDocumentRepository;
        this.kycVerificationRepository = kycVerificationRepository;
        this.validator = validator;
        this.passwordUtil = passwordUtil;
        this.otpUtil = otpUtil;
        this.otpRepository = otpRepository;
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

        var email = jwtUtil.getUserEmailFromJWTToken(cleanToken(authToken));
        var user = iUserRepository.findByEmailAddress(email);
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
                                                                                                                                                