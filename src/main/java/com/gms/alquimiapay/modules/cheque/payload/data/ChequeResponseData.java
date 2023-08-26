package com.gms.alquimiapay.modules.cheque.payload.data;

import lombok.Data;

@Data
public class ChequeResponseData
{
    private String id;
    private String status;
    private String createdAt;
    private String publicLink;
    private String proposedAmount;
    private String currency;
    private String base64Content;
    private boolean isRemotelySaved;
    private String ownerUsername;
    private String ownerEmail;
}
