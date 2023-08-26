package com.gms.alquimiapay.modules.user.controller;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.user.constants.UserApiPaths;
import com.gms.alquimiapay.modules.user.payload.identity.request.TransactionPinChangeRequestPayload;
import com.gms.alquimiapay.modules.user.payload.identity.request.TransactionPinResetOtpVerificationRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.request.*;
import com.gms.alquimiapay.modules.user.payload.kyc.response.UserDetailsResponsePayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.BusinessOfficerRequestPayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.UnlinkBusinessMemberRequestPayload;
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


    @Deprecated
    @Operation(summary = "Checks if the user-handle chosen by the user is available", description = "Checks if the user-handle chosen by the user is available", deprecated = true)
    @GetMapping(value = UserApiPaths.CHECK_USER_HANDLE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleCheckUserHandle(@RequestParam("userHandle") String userHandle, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        return ResponseEntity.ok(userIdentityService.checkUserHandle(userHandle, authToken));
    }


    @Deprecated
    @Operation(summary = "Do KYC registration for an individual user", description = "Do KYC registration for an individual user. Note that this API can only be called when the user-handle of the user has been established. This means the client application using this API must check user-handle via the provided API before proceeding with this call", deprecated = true)
    @PostMapping(value = UserApiPaths.INDIVIDUAL_USER_REG, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleIndividualUserReg(@RequestBody IndividualUserKycRegistrationRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processIndividualUserRegistrationForKyc(requestPayload, authToken));
    }

    @Deprecated
    @Operation(summary = "Do KYC registration for a business user", description = "Do KYC registration for a business user. Note that this API can only be called when the user-handle of the user has been established. This means the client application using this API must check user-handle via the provided API before proceeding with this call", deprecated = true)
    @PostMapping(value = UserApiPaths.BUSINESS_USER_REG, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleBusinessUserReg(@RequestBody BusinessKycRegistrationRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processBusinessUserRegistrationForKyc(requestPayload, authToken));
    }

    @Deprecated
    @Operation(summary = "Upload a KYC document to attach to a user handle for DOC_KYC level verification.", description = "This API uploads a document to enhance user's verification. Note that the front and back of the document should be uploaded. This means that this API should be called twice.", deprecated = true)
    @PostMapping(value = UserApiPaths.UPLOAD_KYC_DOCUMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleDocumentUpload(@RequestBody IndividualDocumentUploadRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processIndividualKycDocumentUpload(authToken, requestPayload));
    }


    @Deprecated
    @Operation(summary = "Starts the request for KYC processing for a business handle", description = "Starts the request for KYC review for a business handle. It should be noted that this API should not be called for individual type users as the system will make the request on their behalf just after they successfully upload a document for verification.", deprecated = true)
    @GetMapping(value = UserApiPaths.REQUEST_KYC, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleKycStartRequest(@RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        return ResponseEntity.ok(userIdentityService.processStartKycRequest(authToken));
    }

    @Deprecated
    @Operation(summary = "Check the complete KYC status of a handle", description = "This API checks and returns a verbose description of the KYC status of a handle", deprecated = true)
    @GetMapping(value = UserApiPaths.CHECK_KYC, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleCheckKycRequest(@RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        return ResponseEntity.ok(userIdentityService.processCheckKycRequest(authToken));
    }

    @Deprecated
    @Operation(summary = "Certifies a beneficial owner prior to business certification", description = "This API certifies a business owner just prior to a business certification.", deprecated = true)
    @PostMapping(value = UserApiPaths.CERTIFY_BENEFICIAL_OWNER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleBeneficialOwnerCertification(@RequestBody CertifyBeneficialOwnerRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processBeneficialOwnerCertification(requestPayload));
    }


    @Deprecated
    @Operation(summary = "Certifies a business after a beneficial owner has been certified for those business that requires certification of beneficial owners", description = "This API certifies a business just after a beneficial owner certification.", deprecated = true)
    @PostMapping(value = UserApiPaths.CERTIFY_BUSINESS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleBusinessCertification(@RequestBody CertifyBusinessRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processBusinessCertification(requestPayload.getBusinessEmail()));
    }

    @Deprecated
    @Operation(summary = "Links an admin officer to a business handle", description = "This API links an admin officer to a business handle", deprecated = true)
    @PostMapping(value = UserApiPaths.LINK_ADMIN_OFFICER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleLinkAdminOfficer(@RequestBody BusinessOfficerRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processBusinessAdministratorRegistration(requestPayload));
    }

    @Deprecated
    @Operation(summary = "Links a controlling officer to a business", description = "This API links a controlling officer to a business handle.", deprecated = true)
    @PostMapping(value = UserApiPaths.LINK_CONTROLLING_OFFICER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleLinkControllingOfficer(@RequestBody BusinessOfficerRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processBusinessControllerOfficerRegistration(requestPayload));
    }

    @Deprecated
    @Operation(summary = "Links a beneficial owner to a business", description = "This API links a beneficial owner to a business handle. This beneficial owner will most likely be certified by its certification token in order to make its associated business to pass full KYB", deprecated = true)
    @PostMapping(value = UserApiPaths.LINK_BENEFICIAL_OWNER_OFFICER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleLinkBeneficialOwner(@RequestBody BusinessOfficerRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processBusinessBeneficialOwnerRegistration(requestPayload));
    }

    @Deprecated
    @Operation(summary = "Unlinks or remove an officer from a business", description = "This API removes an officer from a business", deprecated = true)
    @PostMapping(value = UserApiPaths.UNLINK_BUSINESS_MEMBER, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleUnlinkBusinessMember(@RequestBody UnlinkBusinessMemberRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(userIdentityService.processBusinessMemberUnlinking(requestPayload));
    }

    @Deprecated
    @Operation(summary = "Fetch user details", description = "This API is used to fetch individual or business entity details", deprecated = true)
    @GetMapping(value = UserApiPaths.USER_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetailsResponsePayload> handleUserDetailsRequest(@RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        return ResponseEntity.ok(userIdentityService.processFetchUserDetailsRequest(authToken));
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
