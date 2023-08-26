package com.gms.alquimiapay.integration.external.sila.dto.wallet.request;

import com.gms.alquimiapay.integration.external.sila.dto.identity.request.SilaBaseRequestDTO;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.data.SilaWalletSearchFilter;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaWalletRequestDTO extends SilaBaseRequestDTO
{
    @SerializedName("search_filters")
    private SilaWalletSearchFilter searchFilter;
}
