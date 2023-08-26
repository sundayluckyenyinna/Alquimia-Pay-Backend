package com.gms.alquimiapay.integration.external.circle.dto.deposit.data;

import lombok.Data;

@Data
public class CircleBankDepositResponseData
{
    private String id;
    private String sourceWalletId;
    private CircleDestination destination;
    private CircleAmount amount;
    private CircleFee fee;
    private String status;
    private String createDate;
    private String updateDate;
}
