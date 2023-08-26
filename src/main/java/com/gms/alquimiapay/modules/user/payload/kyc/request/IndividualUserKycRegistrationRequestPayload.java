package com.gms.alquimiapay.modules.user.payload.kyc.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class IndividualUserKycRegistrationRequestPayload
{
    @NotNull(message = "ssn cannot be null")
    @NotEmpty(message = "ssn cannot be empty")
    @NotBlank(message = "ssn cannot be blank")
    @Pattern(regexp = "[0-9]{9}", message = "ssn must be a 9 digit property value")
    @ApiModelProperty(example = "123456789", required = true)
    private String ssn;

    @NotNull(message = "dateOfBirth cannot be null")
    @NotEmpty(message = "dateOfBirth cannot be empty")
    @NotBlank(message = "dateOfBirth cannot be blank")
    @Pattern(regexp = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$", message = "dateOfBirth must be like 2021-01-13")
    @ApiModelProperty(example = "1997-04-27", required = true)
    private String dateOfBirth;

    @NotNull(message = "streetAddress cannot be null")
    @NotEmpty(message = "streetAddress cannot be empty")
    @NotBlank(message = "streetAddress cannot be blank")
    @ApiModelProperty(example = "27, Alhaja Adenike street, Matogbun Ogun state", required = true)
    private String streetAddress;

    @NotNull(message = "addressAlias cannot be null")
    @NotEmpty(message = "addressAlias cannot be empty")
    @NotBlank(message = "addressAlias cannot be blank")
    @ApiModelProperty(example = "home", required = true)
    private String addressAlias;

    @NotNull(message = "city cannot be null")
    @NotEmpty(message = "city cannot be empty")
    @NotBlank(message = "city cannot be blank")
    @ApiModelProperty(example = "Albion horizon", required = true)
    private String city;

    @NotNull(message = "state cannot be null")
    @NotEmpty(message = "state cannot be empty")
    @NotBlank(message = "state cannot be blank")
    @Pattern(regexp = "[A-Z]{2}", message = "state should be a valid 2 capital-letter abbreviation")
    @ApiModelProperty(example = "NY", required = true)
    private String state;

    @NotNull(message = "country cannot be null")
    @NotEmpty(message = "country cannot be empty")
    @NotBlank(message = "country cannot be blank")
    @Pattern(regexp = "[A-Z]{2}", message = "country should be a 2 capital-letter abbreviation")
    @ApiModelProperty(example = "US", required = true)
    private String country;

    @NotNull(message = "zipCode cannot be null")
    @NotEmpty(message = "zipCode cannot be empty")
    @NotBlank(message = "zipCode cannot be blank")
    @ApiModelProperty(example = "144111", required = true)
    private String zipCode;
}
