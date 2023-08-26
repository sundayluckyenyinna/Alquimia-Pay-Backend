package com.gms.alquimiapay.modules.report.payload.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ReportRequestPayload
{
    @NotNull(message = "adminUsername cannot be null")
    @NotBlank(message = "adminUsername cannot be blank")
    @NotEmpty(message = "adminUsername cannot be empty")
    private String adminUser;

    @NotNull(message = "adminPassword cannot be null")
    @NotBlank(message = "adminPassword cannot be blank")
    @NotEmpty(message = "adminPassword cannot be empty")
    private String adminPasscode;

    @NotNull(message = "startDate cannot be null")
    @NotBlank(message = "startDate cannot be blank")
    @NotEmpty(message = "startDate cannot be empty")
    @ApiModelProperty(example = "2023-08-15")
    private String startDate;

    @ApiModelProperty(example = "2023-09-15")
    private String endDate;
}
