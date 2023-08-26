package com.gms.alquimiapay.webhook.dto.circle;

import com.gms.alquimiapay.integration.external.circle.dto.deposit.data.CircleAmount;
import com.gms.alquimiapay.integration.external.circle.dto.deposit.data.CircleFee;
import com.gms.alquimiapay.integration.external.circle.dto.transaction.data.TransactionParty;
import lombok.Data;

@Data
public class CircleWebhookPaymentDTO
{
    private String id;
    private String merchantId;
    private String merchantWalletId;
    CircleAmount amount;
    TransactionParty source;
    String description;
    String status;
    CircleFee fees;
    private String trackingRef;
    private String errorCode;
    private CircleMetadata metadata;
    private String createDate;
    private String updateDate;
}
