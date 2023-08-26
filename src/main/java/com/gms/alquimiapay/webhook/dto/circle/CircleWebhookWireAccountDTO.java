package com.gms.alquimiapay.webhook.dto.circle;

import com.gms.alquimiapay.integration.external.circle.dto.account.data.CircleAccountBillingDetails;
import com.gms.alquimiapay.integration.external.circle.dto.account.data.CircleBankAddress;
import lombok.Data;

@Data
public class CircleWebhookWireAccountDTO
{
    private String id;
    private String type;
    private String status;
    private String description;
    private String trackingRef;
    private String virtualAccountEnabled;
    private String fingerprint;
    private CircleAccountBillingDetails billingDetails;
    private CircleBankAddress bankAddress;
    private String createDate;
    private String updateDate;
}
