package com.gms.alquimiapay.webhook.dto.data;

import lombok.Data;

@Data
public class KycEventUpdateData
{
    private String kyc;
    private String outcome;
    private String entity;
}
