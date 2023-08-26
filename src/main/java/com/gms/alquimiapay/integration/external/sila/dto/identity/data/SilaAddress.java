package com.gms.alquimiapay.integration.external.sila.dto.identity.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaAddress
{
    @SerializedName("address_alias")
    private String addressAlias;

    @SerializedName("street_address_1")
    private String streetAddress1;

    private String city;

    private String state;

    private String country;

    @SerializedName("postal_code")
    private String postalCode;

    @SerializedName("added_epoch")
    private Long addedEpoch;

    @SerializedName("modified_epoch")
    private Long modifiedEpoch;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("nickname")
    private String nickName;

    @SerializedName("street_address_2")
    private String streetAddress2;
}
