package com.gms.alquimiapay.modules.report.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlquimiaRemittanceDTO
{
    private String internalTransactionRef;

    private String externalTransactionRef;

    private String noRemittance;

    private BigDecimal sendAmount;

    private String sendCurrency;

    private Double exchangeRate;

    private BigDecimal paidAmount;

    private String paidCurrency;

    private String sendType;

    private String accountType;

    private String accountNumber;

    private String senderFirstName;

    private String senderSecondName;

    private String senderLastName;

    private String senderSecondLastName;

    private String senderHomePhone;

    private String senderWorkPhone;

    private String senderAddress;

    private String senderGender;

    private String senderBirthday;

    private String beneficiaryFirstName;

    private String beneficiarySecondName;

    private String beneficiaryLastName;

    private String beneficiarySecondLastName;

    private String beneficiaryDepartment;

    private String beneficiaryOccupation;

    private String beneficiaryAddress;

    private String submissionStatus;

    private String failureReason;

    private String createdAt;

    private String updatedAt;
}
