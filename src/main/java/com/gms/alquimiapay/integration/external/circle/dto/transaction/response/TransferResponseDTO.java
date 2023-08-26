package com.gms.alquimiapay.integration.external.circle.dto.transaction.response;

import com.gms.alquimiapay.integration.external.circle.dto.deposit.data.CircleAmount;
import com.gms.alquimiapay.integration.external.circle.dto.transaction.data.TransactionParty;
import lombok.Data;

@Data
public class TransferResponseDTO {

    private String id;
    private TransactionParty source;
    private TransactionParty destination;
    private String status;
    private CircleAmount amount;
    private String transactionHash;
    private String errorCode;
    private String createDate;
}
