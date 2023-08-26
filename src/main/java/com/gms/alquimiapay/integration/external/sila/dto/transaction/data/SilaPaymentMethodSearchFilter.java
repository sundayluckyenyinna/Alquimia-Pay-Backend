package com.gms.alquimiapay.integration.external.sila.dto.transaction.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class SilaPaymentMethodSearchFilter
{
    @SerializedName("payment_method_types")
    private List<String> paymentMethodTypes = List.of("blockchain_address", "bank_account", "card", "ledger_account");

    @SerializedName("page")
    private Integer pageNumber = 1;

    @SerializedName("per_page")
    private Integer pageSize = 10;
}
