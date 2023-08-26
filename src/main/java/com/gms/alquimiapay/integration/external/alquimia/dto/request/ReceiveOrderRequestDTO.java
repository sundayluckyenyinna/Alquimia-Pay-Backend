package com.gms.alquimiapay.integration.external.alquimia.dto.request;

import com.gms.alquimiapay.integration.external.alquimia.dto.data.AlquimiaUser;
import com.gms.alquimiapay.integration.external.alquimia.dto.data.Beneficiary;
import com.gms.alquimiapay.integration.external.alquimia.dto.data.Remittance;
import com.gms.alquimiapay.integration.external.alquimia.dto.data.Sender;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiveOrderRequestDTO
{
    @SerializedName("User") private AlquimiaUser user;
    @SerializedName("Remittance") private Remittance remittance;
    @SerializedName("Sender") private Sender sender;
    @SerializedName("Beneficiary") private Beneficiary beneficiary;
}
