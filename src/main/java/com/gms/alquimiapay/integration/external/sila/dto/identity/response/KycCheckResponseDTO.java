package com.gms.alquimiapay.integration.external.sila.dto.identity.response;

import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class KycCheckResponseDTO extends GenericSilaResponseDTO
{

    @SerializedName("verification_status")
    private String verificationStatus;

    @SerializedName("documents_required_verification_ids")
    private List<String> documentRequiredVerificationIds;

    @SerializedName("valid_kyc_levels")
    private List<String> validKycLevels;
}
