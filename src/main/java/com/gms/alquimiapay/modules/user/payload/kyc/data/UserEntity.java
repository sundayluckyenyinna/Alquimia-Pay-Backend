package com.gms.alquimiapay.modules.user.payload.kyc.data;

import lombok.Data;

@Data
public class UserEntity
{
    private String birthdate;

    private String entityName;

    private String firstName;

    private String lastName;

    private String type;

    private String businessType;

    private String businessTypeUUID;

    private String doingBusinessAs;

    private String businessWebsite;

    private String relationship;

    private Long createdEpoch;
}
