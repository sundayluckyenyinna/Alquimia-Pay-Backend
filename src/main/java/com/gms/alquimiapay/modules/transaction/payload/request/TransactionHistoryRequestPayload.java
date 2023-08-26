package com.gms.alquimiapay.modules.transaction.payload.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class TransactionHistoryRequestPayload
{

    @ApiModelProperty(example = "2023-06-15")
    private String startDate;

    @ApiModelProperty(example = "2023-06-16")
    private String endDate;

    @Pattern(regexp = "^(DEPOSIT|WITHDRAWAL|ALL)$", message = "historyType must be one of DEPOSIT, WITHDRAWAL, ALL")
    @ApiModelProperty(example = "DEPOSIT", allowableValues = "DEPOSIT, WITHDRAWAL, ALL")
    private String historyType;

    private Long limit;
}
