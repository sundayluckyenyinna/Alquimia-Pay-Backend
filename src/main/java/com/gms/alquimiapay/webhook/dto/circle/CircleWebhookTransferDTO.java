package com.gms.alquimiapay.webhook.dto.circle;

import com.gms.alquimiapay.integration.external.circle.dto.deposit.data.CircleAmount;
import com.gms.alquimiapay.integration.external.circle.dto.deposit.data.CircleDestination;
import com.gms.alquimiapay.integration.external.circle.dto.transaction.data.TransactionParty;
import lombok.Data;

@Data
public class CircleWebhookTransferDTO
{
    private String id;
    private TransactionParty source;
    private CircleDestination destination;
    private CircleAmount amount;
    private String transactionHash;
    private String status;
    private String createDate;
}
