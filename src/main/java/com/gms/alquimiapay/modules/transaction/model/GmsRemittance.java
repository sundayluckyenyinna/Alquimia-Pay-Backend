package com.gms.alquimiapay.modules.transaction.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "gms_remittance")
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GmsRemittance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "internal_ref", columnDefinition = "text")
    private String internalRef;

    @Column(name = "internal_trans_ref", columnDefinition = "text")
    private String internalTransactionRef;

    @Column(name = "external_trans_ref", columnDefinition = "text")
    private String externalTransactionRef;

    @Column(name = "remittance_no", columnDefinition = "text")
    private String noRemittance;

    @Column(name = "send_amount")
    private BigDecimal sendAmount;

    @Column(name = "send_currency")
    private String sendCurrency;

    @Column(name = "exchange_rate")
    private Double exchangeRate;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Column(name = "paid_currency")
    private String paidCurrency;

    @Column(name = "send_type")
    private String sendType;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "sender_first_name")
    private String senderFirstName;

    @Column(name = "sender_second_name")
    private String senderSecondName;

    @Column(name = "sender_last_name")
    private String senderLastName;

    @Column(name = "sender_second_last_name")
    private String senderSecondLastName;

    @Column(name = "sender_home_phone")
    private String senderHomePhone;

    @Column(name = "sender_work_phone")
    private String senderWorkPhone;

    @Column(name = "sender_address", columnDefinition = "text")
    private String senderAddress;

    @Column(name = "sender_gender")
    private String senderGender;

    @Column(name = "sender_birthday")
    private String senderBirthday;

    @Column(name = "beneficiary_first_name")
    private String beneficiaryFirstName;

    @Column(name = "beneficiary_second_name")
    private String beneficiarySecondName;

    @Column(name = "beneficiary_last_name")
    private String beneficiaryLastName;

    @Column(name = "beneficiary_second_last_name")
    private String beneficiarySecondLastName;

    @Column(name = "beneficiary_department")
    private String beneficiaryDepartment;

    @Column(name = "beneficiary_occupation")
    private String beneficiaryOccupation;

    @Column(name = "beneficiary_address", columnDefinition = "text")
    private String beneficiaryAddress;

    @Column(name = "user_login_json", columnDefinition = "text")
    private  String userLoginJson;

    @Column(name = "remittance_json", columnDefinition = "text")
    private String remittanceJson;

    @Column(name = "sender_json", columnDefinition = "text")
    private String senderJson;

    @Column(name = "beneficiary_json", columnDefinition = "text")
    private String beneficiaryJson;

    @Column(name = "submission_status")
    private String submissionStatus;

    @Column(name = "failure_reason", columnDefinition = "text")
    private String failureReason;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;
}
