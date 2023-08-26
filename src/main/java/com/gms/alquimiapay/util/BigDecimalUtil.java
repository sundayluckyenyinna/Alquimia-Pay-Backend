package com.gms.alquimiapay.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil
{
    private static final Integer ROUND_UP_DIGIT = 2;

    public static BigDecimal from(String value){
        return new BigDecimal(value).setScale(ROUND_UP_DIGIT, RoundingMode.HALF_UP);
    }

    public static BigDecimal from(Double value){
        return from(String.valueOf(value));
    }
}
