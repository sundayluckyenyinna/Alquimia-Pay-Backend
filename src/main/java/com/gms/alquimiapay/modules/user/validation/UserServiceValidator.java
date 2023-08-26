package com.gms.alquimiapay.modules.user.validation;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.RequestChannel;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.exception.UserRecordNotFoundException;
import com.gms.alquimiapay.modules.user.constants.UserType;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.payload.onboarding.data.UserCredentials;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.payload.ErrorResponse;
import com.gms.alquimiapay.payload.ValidationPayload;
import com.google.gson.Gson;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceValidator implements IUserServiceValidator
{

    private final IUserRepository userRepository;
    private final MessageProvider messageProvider;

    private static final Gson JSON = new Gson();

    @Autowired
    public UserServiceValidator(IUserRepository userRepository, MessageProvider messageProvider) {
        this.userRepository = userRepository;
        this.messageProvider = messageProvider;
    }


    @Override
    public ValidationPayload processUserExistsValidation(UserCredentials userCredentials) {
        ValidationPayload validationPayload = ValidationPayload.getInstance();

        validationPayload.setHasError(false);
        validationPayload.setErrorJson(null);

        ErrorResponse errorResponse = ErrorResponse.getInstance();

        // Check if the user exists by virtue of email address.
        GmsUser gmsUser = userRepository.findByEmailAddress(userCredentials.getEmailAddress());
        if(gmsUser != null){
            errorResponse.setResponseCode(ResponseCode.RECORD_ALREADY_EXISTS_BY_EMAIL);
            errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
            validationPayload.setHasError(true);
            validationPayload.setErrorJson(JSON.toJson(errorResponse));
        }

        // Check if user exist by username.
        gmsUser = userRepository.findByUsername(userCredentials.getUsername());
        if(gmsUser != null){
            errorResponse.setResponseCode(ResponseCode.RECORD_ALREADY_EXISTS_BY_USERNAME);
            errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
            validationPayload.setHasError(true);
            validationPayload.setErrorJson(JSON.toJson(errorResponse));
            return validationPayload;
        }

        // Check if user exist by mobile number.
        gmsUser = userRepository.findByMobileNumber(userCredentials.getMobileNumber());
        if(gmsUser != null){
            errorResponse.setResponseCode(ResponseCode.RECORD_ALREADY_EXISTS_BY_MOBILE);
            errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
            validationPayload.setHasError(true);
            validationPayload.setErrorJson(JSON.toJson(errorResponse));
        }

        return validationPayload;
    }


    @Override
    public ValidationPayload processChannelValidation(@NonNull String channel, String deviceId) {
        ValidationPayload validationPayload = ValidationPayload.getInstance();
        validationPayload.setHasError(false);
        validationPayload.setErrorJson(null);
        ErrorResponse errorResponse = ErrorResponse.getInstance();
        if(channel.equalsIgnoreCase(RequestChannel.MOBILE.name())){
            if(deviceId == null || deviceId.isEmpty() || deviceId.isBlank()){
                errorResponse.setResponseCode(ResponseCode.INVALID_DEVICE_ID);
                errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
                validationPayload.setHasError(true);
                validationPayload.setErrorJson(JSON.toJson(errorResponse));
            }
        }
        return validationPayload;
    }

    @Override
    public ValidationPayload processUserExistsByEmailValidation(String emailAddress) {
        ValidationPayload validationPayload = ValidationPayload.getInstance();
        validationPayload.setHasError(false);
        validationPayload.setErrorJson(null);

        GmsUser user = userRepository.findByEmailAddress(emailAddress);
        if(user == null){
            ErrorResponse errorResponse = ErrorResponse.getInstance();
            errorResponse.setResponseCode(ResponseCode.RECORD_NOT_FOUND);
            errorResponse.setResponseMessage(messageProvider.getMessage(errorResponse.getResponseCode()));
            validationPayload.setHasError(true);
            validationPayload.setErrorJson(JSON.toJson(errorResponse));
        }

        validationPayload.setExtraItem(user);
        return validationPayload;
    }

    @Override
    public ValidationPayload processUserTypeValidation(String userTye, String businessName){
        ValidationPayload validationPayload = ValidationPayload.getInstance();
        validationPayload.setHasError(false);

        if(userTye != null && userTye.toUpperCase().equalsIgnoreCase(UserType.BUSINESS.name())){
            if(businessName == null || businessName.isEmpty() || businessName.isBlank()){
                ErrorResponse errorResponse = ErrorResponse.getInstance();
                errorResponse.setResponseCode(ResponseCode.BAD_MODEL);
                errorResponse.setResponseMessage(messageProvider.getMessage(ResponseCode.BAD_USER_TYPE));
                validationPayload.setHasError(true);
                validationPayload.setErrorJson(JSON.toJson(errorResponse));
            }
        }

        return validationPayload;
    }

    @Override
    public void validateUserExistValidationThrowException(String email){
        ValidationPayload validationPayload = this.processUserExistsByEmailValidation(email);
        if(validationPayload.isHasError()){
            String code = ResponseCode.RECORD_NOT_FOUND;
            throw new UserRecordNotFoundException(messageProvider.getMessage(code));
        }
    }
}
