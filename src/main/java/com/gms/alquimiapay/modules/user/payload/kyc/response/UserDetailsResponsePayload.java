package com.gms.alquimiapay.modules.user.payload.kyc.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gms.alquimiapay.modules.user.payload.kyc.data.*;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsResponsePayload extends BaseResponse
{
    private UserEntity entity;
    private UserContact contact;
    private UserAddress address;
    private UserIdentity identity;
    private UserKycSummary kycSummary;
    private List<MemberShip> businessAdmins;
    private List<MemberShip> controllingOfficers;
    private List<MemberShip> beneficialOwners;
    private List<UserDocument> documents;
}
