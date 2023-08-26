package com.gms.alquimiapay.integration.external.circle.constant;

public class CircleApiPath
{
    // ACCOUNT
    public static final String WIRE_ACCOUNT_LINKING = "/businessAccount/banks/wires";
    public static final String WIRE_VIRTUAL_ACCOUNT_INSTRUCTION = "/businessAccount/banks/wires/{id}/instructions";
    public static final String WIRE_SINGLE_ACCOUNT = "/businessAccount/banks/wires/{id}";

    // DEPOSIT
    public static final String WIRE_DEPOSITS = "/businessAccount/deposits";

    // ADDRESS
    public static final String BLOCKCHAIN_RECIPIENT_ADDRESS = "/businessAccount/wallets/addresses/recipient";

    // TRANSACTION
    public static final String ACCOUNT_TRANSFER = "/businessAccount/transfers";
    public static final String ACCOUNT_TRANSFER_STATUS = "/businessAccount/transfers/{id}";
}
