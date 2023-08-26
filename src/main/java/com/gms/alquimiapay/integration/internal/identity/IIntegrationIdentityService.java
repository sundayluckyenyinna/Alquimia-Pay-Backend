package com.gms.alquimiapay.integration.internal.identity;

import com.gms.alquimiapay.modules.kyc.model.UserUploadDocument;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.payload.BaseResponse;

import java.util.List;


public interface IIntegrationIdentityService {

    BaseResponse checkUserHandle(String userHandle);
    BaseResponse processUserRegistrationToThirdParty(GmsUser user);

    BaseResponse processBusinessTypesRequest();

    BaseResponse processBusinessCategoriesRequest();

    BaseResponse processDocumentTypesRequest();

    BaseResponse processBusinessRolesRequest();

    BaseResponse processKYCRequest(GmsUser user);

    BaseResponse processKycRequestChecking(GmsUser user);

    BaseResponse processSingleKycDocumentUpload(UserUploadDocument document);

    BaseResponse processMultipleKycDocumentUpload(List<UserUploadDocument> documents);

    BaseResponse processBusinessAdministratorRegistration(String businessHandle, String officerHandle);

    BaseResponse processBusinessControllingOfficerRegistration(String businessHandle, String officerHandle, String adminHandle);

    BaseResponse processBusinessBeneficialOwnerRegistration(String businessHandle, String officerHandle, String adminHandle, String details, double ownerStake);

    BaseResponse processBusinessMemberUnlinking(String userHandle, String businessHandle, String role);

    BaseResponse processGetUserEntity(String userHandle);

    BaseResponse processBeneficialOwnerCertification(String ownerHandle, String adminHandle, String businessHandle, String certToken);

    BaseResponse processBusinessCertification(String adminHandle, String businessHandle);
}
