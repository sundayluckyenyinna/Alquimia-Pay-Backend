package com.gms.alquimiapay.modules.user.controller;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.user.constants.UserApiPaths;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.*;
import com.gms.alquimiapay.modules.user.payload.onboarding.response.SignupOtpVerificationResponsePayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.response.UserLoginResponsePayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.response.UserSignupResponsePayload;
import com.gms.alquimiapay.modules.user.service.IUserOnboardingService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.validation.GenericValidator;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Api(tags = "User Onboarding services and Management", description = "Provides functionalities for all user onboarding services and management")
@RestController
@RequestMapping(UserApiPaths.USER_BASE_URL)
public class UserOnboardingRestController
{
    private final IUserOnboardingService userService;
    private final GenericValidator genericValidator;
    private static final Gson JSON = new Gson();

    @Autowired
    public UserOnboardingRestController(IUserOnboardingService userService, GenericValidator genericValidator) {
        this.userService = userService;
        this.genericValidator = genericValidator;
    }


    @Operation(summary = "Signup a new user to the system", description = "Signup a new user to the system> This endpoint automatically sends verification email to the user.")
    @PostMapping(value = UserApiPaths.USER_SIGN_UP, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserSignupResponsePayload> handleUserSignup(@RequestBody UserSignupRequestPayload requestPayload){
        genericValidator.doModelValidationThrowException(requestPayload);
        String serviceResponse = userService.processSignupUser(requestPayload);
        return ResponseEntity.ok(JSON.fromJson(serviceResponse, UserSignupResponsePayload.class));
    }

    @Operation(summary = "Verify otp for user signup", description = "Verify otp for user signup")
    @PostMapping(value = UserApiPaths.VERIFY_SIGNUP_OTP, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignupOtpVerificationResponsePayload> handleUserSignupOtpVerification(@RequestBody SignupOtpVerificationRequestPayload requestPayload){
        genericValidator.doModelValidationThrowException(requestPayload);
        String serviceResponse = userService.processSignupOtpVerification(requestPayload);
        return ResponseEntity.ok(JSON.fromJson(serviceResponse, SignupOtpVerificationResponsePayload.class));
    }

    @Operation(summary = "Process user login with provision for new authorization credentials", description = "Process user login with provision for new authorization credentials")
    @PostMapping(value = UserApiPaths.USER_LOGIN, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLoginResponsePayload> handleUserLogin(@RequestBody UserLoginRequestPayload requestPayload){
        genericValidator.doModelValidationThrowException(requestPayload);
        String serviceResponse = userService.processUserLogin(requestPayload);
        return ResponseEntity.ok(JSON.fromJson(serviceResponse, UserLoginResponsePayload.class));
    }

    @Operation(summary = "Process user logout", description = "Process user logout. Note that this request forcefully expires the login authorization token of the current user.")
    @PostMapping(value = UserApiPaths.USER_LOGOUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleUserLogout(@RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        String serviceResponse = userService.processUserLogout(authToken);
        return ResponseEntity.ok(JSON.fromJson(serviceResponse, UserLoginResponsePayload.class));
    }

    @Operation(summary = "Asynchronously sends otp to user email to initiate forget-password processing", description = "Asynchronously sends otp to user email to initiate forget-password processing")
    @GetMapping(value = UserApiPaths.FORGOT_PASSWORD_OTP_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleForgotPasswordOtpRequest(@RequestParam("email") String email){
        String serviceResponse = userService.processForgotPasswordOtpRequest(email);
        return ResponseEntity.ok(JSON.fromJson(serviceResponse, BaseResponse.class));
    }

    @Operation(summary = "Verifies the otp requested by the user for forgot-password", description = "Verifies the otp requested by the user for forgot-password. The user's password will automatically be reset when otp validation passes.")
    @PostMapping(value = UserApiPaths.FORGOT_PASSWORD_OTP_VERIFY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleForgetPasswordOtpVerification(@RequestBody ForgotPasswordOtpVerifyRequestPayload requestPayload){
        genericValidator.doModelValidationThrowException(requestPayload);
        String serviceResponse = userService.processForgotPasswordOtpVerification(requestPayload.getEmail(), requestPayload);
        return ResponseEntity.ok(JSON.fromJson(serviceResponse, BaseResponse.class));
    }

    @Operation(summary = "verifies the otp sent by the user during new device login.", description = "verifies the otp sent by the user during new device login. The new device linking otp was sent automatically by the system during new device login by the user.")
    @PostMapping(value = UserApiPaths.NEW_DEVICE_OTP_VERIFICATION, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleNewDeviceOtpVerification(@RequestBody DeviceLinkOtpVerifyRequestPayload requestPayload){
        genericValidator.doModelValidationThrowException(requestPayload);
        String serviceResponse = userService.processNewDeviceLinkOtpVerification(requestPayload.getEmail(), requestPayload.getNewDeviceId(), requestPayload.getOtp());
        return ResponseEntity.ok(JSON.fromJson(serviceResponse, BaseResponse.class));
    }
}
