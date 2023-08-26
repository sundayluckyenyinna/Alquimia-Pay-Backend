package com.gms.alquimiapay.modules.user.payload.onboarding.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserSignupRequestPayload
{
    @NotNull(message = "email address cannot be null")
    @NotBlank(message = "email address cannot be blank")
    @NotEmpty(message = "email address cannot be empty")
    @Email(message = "email address must be a valid email with a valid email extension")
    @ApiModelProperty(position = 0)
    private String emailAddress;

    @NotNull(message = "username cannot be null")
    @NotBlank(message = "username cannot be blank")
    @NotEmpty(message = "username cannot be empty")
    @ApiModelProperty(position = 1, name = "Username of individual user or username of the administrator, owner or controlling officer of business", value = "Username of individual user or username of the administrator, owner or controlling officer of business")
    private String username;

    @NotNull(message = "firstName cannot be null")
    @NotBlank(message = "firstName cannot be blank")
    @NotEmpty(message = "firstName cannot be empty")
    @ApiModelProperty(position = 2)
    private String firstName;

    @NotNull(message = "lastName cannot be null")
    @NotBlank(message = "lastName cannot be blank")
    @NotEmpty(message = "lastName cannot be empty")
    @ApiModelProperty(position = 3)
    private String lastName;

    @NotNull(message = "phoneNumber cannot be null")
    @NotBlank(message = "phoneNumber cannot be blank")
    @NotEmpty(message = "phoneNumber cannot be empty")
    @ApiModelProperty(position = 4)
    private String phoneNumber;

    @NotNull(message = "password cannot be null")
    @NotBlank(message = "password cannot be blank")
    @NotEmpty(message = "password cannot be empty")
    @ApiModelProperty(position = 5)
    private String password;

    @NotNull(message = "transactionPin cannot be null")
    @NotBlank(message = "transactionPin cannot be blank")
    @NotEmpty(message = "transactionPin cannot be empty")
    @ApiModelProperty(position = 6)
    private String transactionPin;

    @ApiModelProperty(position = 7)
    private String deviceId;

    @NotNull(message = "channel cannot be null")
    @NotBlank(message = "channel cannot be blank")
    @NotEmpty(message = "channel cannot be empty")
    @Pattern(regexp = "^(MOBILE|WEB|CONSOLE)$", message = "channel must be of MOBILE, WEB or CONSOLE")
    @ApiModelProperty(position = 8)
    private String channel;

    @NotNull(message = "userType cannot be null")
    @NotBlank(message = "userType cannot be blank")
    @NotEmpty(message = "userType cannot be empty")
    @Pattern(regexp = "^(INDIVIDUAL|BUSINESS)$", message = "userType must be of INDIVIDUAL or BUSINESS")
    @ApiModelProperty(position = 9)
    private String userType;

    @ApiModelProperty(position = 10)
    private String businessName;
}
