package com.gms.alquimiapay.modules.user.payload.kyc.data;

import lombok.Data;

@Data
public class MemberShip
{
    private String businessHandle;

    private String entityName;

    private String role;

    private String details;

    private String ownerShipStake;

    private String certificationToken;
}
