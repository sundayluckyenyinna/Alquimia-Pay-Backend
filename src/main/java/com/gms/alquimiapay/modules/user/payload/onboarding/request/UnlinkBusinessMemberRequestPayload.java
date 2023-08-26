package com.gms.alquimiapay.modules.user.payload.onboarding.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UnlinkBusinessMemberRequestPayload
{
    @NotNull(message = "officerEmail cannot be null")
    @NotEmpty(message = "officerEmail cannot be empty")
    @NotBlank(message = "officerEmail cannot be blank")
    @ApiModelProperty(example = "officeremail@gmail.com")
    private String officerEmail;

    @NotNull(message = "businessEmail cannot be null")
    @NotEmpty(message = "businessEmail cannot be empty")
    @NotBlank(message = "businessEmail cannot be blank")
    @ApiModelProperty(example = "businessemail@gmail.com")
    private String businessEmail;

    @NotNull(message = "role cannot be null")
    @NotEmpty(message = "role cannot be empty")
    @NotBlank(message = "role cannot be blank")
    @ApiModelProperty(example = "beneficial_owner")
    @Pattern(regexp = "^(ADMINISTRATOR|CONTROLLING_OFFICER|BENEFICIAL_OWNER)$", message = "role must either be of ADMINISTRATOR, CONTROLLING_OFFICER or BENEFICIAL_OWNER")
    private String role;
}
