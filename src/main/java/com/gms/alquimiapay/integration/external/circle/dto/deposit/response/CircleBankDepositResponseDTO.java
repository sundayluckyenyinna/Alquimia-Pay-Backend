package com.gms.alquimiapay.integration.external.circle.dto.deposit.response;

import com.gms.alquimiapay.integration.external.circle.dto.deposit.data.CircleBankDepositResponseData;
import lombok.Data;

import java.util.List;

@Data
public class CircleBankDepositResponseDTO
{
    private List<CircleBankDepositResponseData> data;
}
