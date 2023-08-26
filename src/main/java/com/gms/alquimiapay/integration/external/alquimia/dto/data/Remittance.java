package com.gms.alquimiapay.integration.external.alquimia.dto.data;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Remittance
{
    private String noRemittance;
    private String noSequence;
    private BigDecimal sendAmount;
    private String sendCurrency;
    private Double exchangeRate;
    private BigDecimal paidAmount;
    private String paidCurrency;
    private String sendType;
    private String accountType;
    private String accountNumber;
}
