package com.gms.alquimiapay.integration.external.circle.dto.account.response;

import com.gms.alquimiapay.integration.external.circle.dto.account.data.CircleWireAccountInstructionBeneficiaryBankData;
import com.gms.alquimiapay.integration.external.circle.dto.account.data.CircleWireAccountInstructionBeneficiaryData;
import lombok.Data;

@Data
public class CircleCreateWireAccountInstructionResponseDTO
{
    private String trackingRef;
    private CircleWireAccountInstructionBeneficiaryData beneficiary;
    private Boolean virtualAccountEnabled;
    private CircleWireAccountInstructionBeneficiaryBankData beneficiaryBank;
}
