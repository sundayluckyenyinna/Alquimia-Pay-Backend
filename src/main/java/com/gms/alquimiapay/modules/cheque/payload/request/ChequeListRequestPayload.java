package com.gms.alquimiapay.modules.cheque.payload.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ChequeListRequestPayload
{
    @NotNull(message = "startDate cannot be null")
    @NotBlank(message = "startDate cannot be blank")
    @NotEmpty(message = "startDate cannot be empty")
    @Pattern(regexp = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$", message = "startDate must be like 1999-09-19")
    @ApiModelProperty(example = "2023-07-16")
    private String startDate;

    private String endDate;

    private String status;

    @NotNull(message = "adminUser cannot be null")
    @NotEmpty(message = "adminUser cannot be empty")
    @NotBlank(message = "adminUser cannot be blank")
    private String adminUser;

    @NotNull(message = "adminPasscode cannot be null")
    @NotEmpty(message = "adminPasscode cannot be empty")
    @NotBlank(message = "adminPasscode cannot be blank")
    private String adminPasscode;
}
