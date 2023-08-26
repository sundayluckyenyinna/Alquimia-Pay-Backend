package com.gms.alquimiapay.webhook.constants;

public enum CircleNotificationType
{
    PAYMENTS("payments"),
    CHARGE_BACKS("chargebacks"),
    PAYOUT("payouts"),
    RETURNS("returns"),
    SETTLEMENTS("settlements"),
    CARDS("cards"),
    WIRE("wire"),
    TRANSFERS("transfers"),
    CONVERSIONS("conversions");


    public final String type;

    CircleNotificationType(String type){
        this.type = type;
    }
}
