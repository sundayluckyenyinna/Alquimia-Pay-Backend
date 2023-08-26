package com.gms.alquimiapay.integration.external.circle.dto.account.response;

import com.gms.alquimiapay.integration.external.circle.dto.account.data.CircleAccountBillingDetails;
import com.gms.alquimiapay.modules.account.payload.data.BankAddress;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CircleLinkWireAccountResponseDTO
{
    private String id;
    private String status;
    private String description;
    private String trackingRef;
    @SerializedName("fingerprint")
    private String fingerPrint;
    private CircleAccountBillingDetails billingDetails;
    private BankAddress bankAddress;
    private String createDate;
    private String updateDate;
}
