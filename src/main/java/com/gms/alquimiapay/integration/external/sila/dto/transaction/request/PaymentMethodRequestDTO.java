package com.gms.alquimiapay.integration.external.sila.dto.transaction.request;

import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaIdentity;
import com.gms.alquimiapay.integration.external.sila.dto.transaction.data.SilaPaymentMethodSearchFilter;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PaymentMethodRequestDTO
{
    private SilaIdentity header;

    @SerializedName("search_filters")
    private SilaPaymentMethodSearchFilter searchFilters;
}
