package com.gms.alquimiapay.integration.external.circle.dto.transaction.data;

import lombok.Data;

import java.util.List;

@Data
public class BlockchainAddressListDTO
{
    private List<BlockchainAddressDTO> data;
}
