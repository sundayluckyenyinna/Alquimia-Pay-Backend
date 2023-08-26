package com.gms.alquimiapay.modules.user.payload.kyc.data;

import lombok.Data;

@Data
public class UserIdentity
{
    private String identityAlias;

    private String identityValue;

    private Long addedEpoch;

    private Long modifiedEpoch;

    private String uuid;
}
