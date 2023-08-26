package com.gms.alquimiapay.modules.user.payload.kyc.data;

import lombok.Data;

@Data
public class UserAddress
{
    private String addressAlias;

    private String streetAddress1;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private Long addedEpoch;

    private Long modifiedEpoch;

    private String uuid;

    private String nickName;

    private String streetAddress2;
}
