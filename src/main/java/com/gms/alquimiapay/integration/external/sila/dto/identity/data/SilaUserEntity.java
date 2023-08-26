package com.gms.alquimiapay.integration.external.sila.dto.identity.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SilaUserEntity
{
    private String birthdate;

    @SerializedName("entity_name")
    private String entityName;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    private String type;

    @SerializedName("business_type")
    private String businessType;

    @SerializedName("business_type_uuid")
    private String businessTypeUUID;

    @SerializedName("naics_code")
    private String naicsCode;

    @SerializedName("doing_business_as")
    private String doingBusinessAs;

    @SerializedName("business_website")
    private String businessWebsite;

    private String relationship;

    @SerializedName("created_epoch")
    private Long createdEpoch;

}
