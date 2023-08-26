package com.gms.alquimiapay.integration.external.circle.dto.transaction.data;

import lombok.Data;

@Data
public class TransactionParty
{
    private String type;
    private String id;
    private String address;
    private String chain;
}
