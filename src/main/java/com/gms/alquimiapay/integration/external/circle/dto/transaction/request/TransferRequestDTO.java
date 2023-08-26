package com.gms.alquimiapay.integration.external.circle.dto.transaction.request;

import com.gms.alquimiapay.integration.external.circle.dto.deposit.data.CircleAmount;
import com.gms.alquimiapay.integration.external.circle.dto.transaction.data.TransactionAddress;
import lombok.Data;

@Data
public class TransferRequestDTO
{
    private String idempotencyKey;
    private TransactionAddress destination;
    private CircleAmount amount;
}
