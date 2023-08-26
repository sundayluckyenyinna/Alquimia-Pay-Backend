package com.gms.alquimiapay.integration.external.circle.dto.account.request;

import com.gms.alquimiapay.integration.external.circle.dto.account.data.CircleAccountBillingDetails;
import com.gms.alquimiapay.integration.external.circle.dto.account.data.CircleBankAddress;
import lombok.Data;

@Data
public class CircleCreateVirtualAccountRequestDTO
{
    private String idempotencyKey;
    private String accountNumber;
    private String routingNumber;
    private CircleAccountBillingDetails billingDetails;
    private CircleBankAddress bankAddress;
}
