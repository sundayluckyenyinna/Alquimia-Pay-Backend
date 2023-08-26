package com.gms.alquimiapay.modules.transaction.service;

import com.gms.alquimiapay.constants.CurrencyCode;

public class ExchangeRateFactory
{
    public static Double getDefaultExchangeRate(String fromCurrency, String toCurrency){
        if(fromCurrency.equalsIgnoreCase(CurrencyCode.USD.name()) && toCurrency.equalsIgnoreCase(CurrencyCode.MXN.name()))
            return 16.99;
        else
            return 1.0;
    }
}
