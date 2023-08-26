package com.gms.alquimiapay.modules.user.payload.kyc.data;

import lombok.Data;


@Data
public class UserKycSummary
{
    private String vendor;

    private String createdAt;

    private String updatedAt;

    private String status;

    private String kycLevel;

    private String externalReference;

    private String internalReference;

    private String kycTier;
}
