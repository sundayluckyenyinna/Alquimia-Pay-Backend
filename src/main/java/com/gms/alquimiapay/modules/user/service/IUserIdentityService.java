package com.gms.alquimiapay.modules.user.service;

import com.gms.alquimiapay.modules.user.payload.identity.request.TransactionPinChangeRequestPayload;
import com.gms.alquimiapay.modules.user.payload.identity.request.TransactionPinResetOtpVerificationRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.request.BusinessKycRegistrationRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.request.IndividualDocumentUploadRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.request.IndividualUserKycRegistrationRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.request.CertifyBeneficialOwnerRequestPayload;
import com.gms.alquimiapay.modules.user.payload.kyc.response.UserDetailsResponsePayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.BusinessOfficerRequestPayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.UnlinkBusinessMemberRequestPayload;
import com.gms.alquimiapay.payload.BaseResponse;

public interface IUserIdentityService {
    BaseResponse checkUserHandle(String userHandle, String token);

    BaseResponse processStartKycRequest(String authToken);

    BaseResponse processCheckKycRequest(String authToken);

    BaseResponse processIndividualUserRegistrationForKyc(IndividualUserKycRegistrationRequestPayload requestPayload, String token);
    BaseResponse processBusinessUserRegistrationForKyc(BusinessKycRegistrationRequestPayload requestPayload, String token);

    BaseResponse processBusinessAdministratorRegistration(BusinessOfficerRequestPayload requestPayload);

    BaseResponse processBusinessControllerOfficerRegistration(BusinessOfficerRequestPayload requestPayload);

    BaseResponse processBusinessBeneficialOwnerRegistration(BusinessOfficerRequestPayload requestPayload);

    BaseResponse processBusinessMemberUnlinking(UnlinkBusinessMemberRequestPayload requestPayload);

    UserDetailsResponsePayload processFetchUserDetailsRequest(String authToken);

    BaseResponse processBeneficialOwnerCertification(CertifyBeneficialOwnerRequestPayload requestPayload);

    BaseResponse processBusinessCertification(String businessEmail);

    BaseResponse processIndividualKycDocumentUpload(String authToken, IndividualDocumentUploadRequestPayload requestPayload);

    BaseResponse processBusinessTypes();
    BaseResponse processBusinessCategories();

    BaseResponse processDocumentTypes();

    BaseResponse processBusinessRoleRequest();

    BaseResponse processChangePinRequest(String authToken, TransactionPinChangeRequestPayload requestPayload);
    BaseResponse processForgetPinOtpRequest(String authToken, String deviceId);
    BaseResponse processForgetPinOtpVerification(String authToken, TransactionPinResetOtpVerificationRequestPayload requestPayload);
}
