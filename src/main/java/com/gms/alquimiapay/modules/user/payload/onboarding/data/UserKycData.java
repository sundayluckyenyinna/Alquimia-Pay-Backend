package com.gms.alquimiapay.modules.user.payload.onboarding.data;

import lombok.Data;

@Data
public class UserKycData
{
    private String status;
    private String kycLevel;
    private String kycTier;
}
