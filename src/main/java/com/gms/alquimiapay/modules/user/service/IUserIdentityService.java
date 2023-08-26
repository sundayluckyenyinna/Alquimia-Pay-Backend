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

    BaseResponse processChangePinRequest(String authToken, TransactionPinChangeRequestPayload requestPayload);
    BaseResponse processForgetPinOtpRequest(String authToken, String deviceId);
    BaseResponse processForgetPinOtpVerification(String authToken, TransactionPinResetOtpVerificationRequestPayload requestPayload);
}
