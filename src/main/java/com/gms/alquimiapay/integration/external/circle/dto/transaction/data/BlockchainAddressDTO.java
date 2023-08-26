package com.gms.alquimiapay.integration.external.circle.dto.transaction.data;

import lombok.Data;

@Data
public class BlockchainAddressDTO
{
    private String idempotencyKey;
    private String id;
    private String address;
    private String addressTag;
    private String chain;
    private String currency;
    private String description;
}
