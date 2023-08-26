package com.gms.alquimiapay.modules.user.controller;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.user.constants.UserApiPaths;
import com.gms.alquimiapay.modules.user.payload.identity.request.TransactionPinChangeRequestPayload;
import com.gms.alquimiapay.modules.user.payload.identity.request.TransactionPinResetOtpVerificationRequestPayload;
import com.gms.alquimiapay.modules.user.service.IUserIdentityService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.validation.GenericValidator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(UserApiPaths.USER_IDENTITY_BASE)
@Api(tags = "User Identity services and Management", description = "Provides functionalities for all user identity services and management")
public class UserIdentityRestController
{
    private final IUserIdentityService userIdentityService;

    private final GenericValidator genericValidator;

    @Autowired
    public UserIdentityRestController(IUserIdentityService userIdentityService, GenericValidator genericValidator) {
        this.userIdentityService = userIdentityService;
        this.genericValidator = genericValidator;
    }

    @Operation(summary = "Change user pin to a new pin provided the old transaction pin is supplied correctly", description = "Change user pin to a new pin provided the old transaction pin is supplied correctly")
    @PostMapping(value = UserApiPaths.PIN_CHANGE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handlePinChangeRequest(@RequestBody TransactionPinChangeRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processChangePinRequest(authToken, requestPayload));
    }

    @Operation(summary = "Request for OTP for a reset of pin", description = "Request for OTP for a reset of pin. The OTP will automatically be sent to the user's email address")
    @GetMapping(value = UserApiPaths.PIN_RESET_OTP_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handlePinResetOtpRequest(@RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken, @RequestParam("deviceId") String deviceId){
        return ResponseEntity.ok(userIdentityService.processForgetPinOtpRequest(authToken, deviceId));
    }

    @Operation(summary = "Validate the otp sent to the user's email for transaction pin reset.", description = "Validate the otp sent to the user's email for transaction pin reset. If the verification process is successful, the transaction pin of the user will be automatically updated to the supplied new value.")
    @PostMapping(value = UserApiPaths.PIN_RESET_OTP_VERIFICATION, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handlePinResetOtpVerification(@RequestBody TransactionPinResetOtpVerificationRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processForgetPinOtpVerification(authToken, requestPayload));
    }
}
