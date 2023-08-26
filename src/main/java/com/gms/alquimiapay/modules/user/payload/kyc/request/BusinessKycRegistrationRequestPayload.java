package com.gms.alquimiapay.modules.user.payload.kyc.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class BusinessKycRegistrationRequestPayload
{
    @NotNull(message = "legalBusinessName cannot be null")
    @NotEmpty(message = "legalBusinessName cannot be empty")
    @NotBlank(message = "legalBusinessName cannot be blank")
    @ApiModelProperty(example = "Tesla Co-operation Ltd", required = true)
    private String legalBusinessName;

    @NotNull(message = "businessAlias cannot be null")
    @NotEmpty(message = "businessAlias cannot be empty")
    @NotBlank(message = "businessAlias cannot be blank")
    @ApiModelProperty(example = "Tesla Brighter", required = true)
    private String businessAlias;

    @NotNull(message = "businessWebsite cannot be null")
    @NotEmpty(message = "businessWebsite cannot be empty")
    @NotBlank(message = "businessWebsite cannot be blank")
    @ApiModelProperty(example = "https://teslabright/co.uk", required = true)
    private String businessWebsite;

    @NotNull(message = "businessEin cannot be null")
    @NotEmpty(message = "businessEin cannot be empty")
    @NotBlank(message = "businessEin cannot be blank")
    private String businessEin;

    @NotNull(message = "legalBusinessName cannot be null")
    @NotEmpty(message = "legalBusinessName cannot be empty")
    @NotBlank(message = "legalBusinessName cannot be blank")
    @Pattern(regexp = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$", message = "dateOfInc must be like 2021-01-13")
    @ApiModelProperty(example = "1997-04-27", required = true)
    private String dateOfInc;

    @NotNull(message = "businessAddress cannot be null")
    @NotEmpty(message = "businessAddress cannot be empty")
    @NotBlank(message = "businessAddress cannot be blank")
    private String businessAddress;

    @NotNull(message = "addressAlias cannot be null")
    @NotEmpty(message = "addressAlias cannot be empty")
    @NotBlank(message = "addressAlias cannot be blank")
    @ApiModelProperty(example = "home", required = true)
    private String addressAlias;

    @NotNull(message = "city cannot be null")
    @NotEmpty(message = "city cannot be empty")
    @NotBlank(message = "city cannot be blank")
    @ApiModelProperty(example = "New york", required = true)
    private String city;

    @NotNull(message = "state cannot be null")
    @NotEmpty(message = "state cannot be empty")
    @NotBlank(message = "state cannot be blank")
    @Pattern(regexp = "[A-Z]{2}", message = "state should be a valid 2 capital-letter abbreviation")
    @ApiModelProperty(example = "New york", required = true)
    private String state;

    @NotNull(message = "country cannot be null")
    @NotEmpty(message = "country cannot be empty")
    @NotBlank(message = "country cannot be blank")
    @Pattern(regexp = "[A-Z]{2}", message = "country should be a 2 capital-letter abbreviation")
    @ApiModelProperty(example = "New york", required = true)
    private String country;

    @NotNull(message = "zipCode cannot be null")
    @NotEmpty(message = "zipCode cannot be empty")
    @NotBlank(message = "zipCode cannot be blank")
    @ApiModelProperty(example = "New york", required = true)
    private String zipCode;

    @NotNull(message = "businessPhoneNumber cannot be null")
    @NotEmpty(message = "businessPhoneNumber cannot be empty")
    @NotBlank(message = "businessPhoneNumber cannot be blank")
    private String businessPhoneNumber;

    @NotNull(message = "registrationState cannot be null")
    @NotEmpty(message = "registrationState cannot be empty")
    @NotBlank(message = "registrationState cannot be blank")
    @Pattern(regexp = "[A-Z]{2}", message = "state must be 2 capital letter representation of US state")
    @ApiModelProperty(example = "DC")
    private String registrationState;

    @NotNull(message = "businessUUID cannot be null")
    @NotEmpty(message = "businessUUID cannot be empty")
    @NotBlank(message = "businessUUID cannot be blank")
    @ApiModelProperty(example = "0e44178c-6ebd-4aca-be07-51d26f2c13aa")
    private String businessUUID;

    @NotNull(message = "businessCategoryId cannot be null")
    @NotEmpty(message = "businessCategoryId cannot be empty")
    @NotBlank(message = "businessCategoryId cannot be blank")
    @ApiModelProperty(example = "333")
    private String businessCategoryCode;

    @NotNull(message = "requiresCertification cannot be null")
    @NotEmpty(message = "requiresCertification cannot be empty")
    @NotBlank(message = "requiresCertification cannot be blank")
    @ApiModelProperty(example = "true")
    private boolean requiresCertification;
}
