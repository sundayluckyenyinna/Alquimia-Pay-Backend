package com.gms.alquimiapay.integration.external.xe.payload;

import lombok.Data;

import java.util.List;


@Data
public class ExchangeRateSuccessDTO
{
    private String terms;
    private String privacy;
    private double amount;
    private String timestamp;
    private List<ExchangeRateTo> to;


    public static class ExchangeRateTo{
        private String quotecurrency;
        private double mid;

        public String getQuotecurrency() {
            return quotecurrency;
        }

        public void setQuotecurrency(String quotecurrency) {
            this.quotecurrency = quotecurrency;
        }

        public double getMid() {
            return mid;
        }

        public void setMid(double mid) {
            this.mid = mid;
        }
    }
}
