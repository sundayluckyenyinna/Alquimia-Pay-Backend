package com.gms.alquimiapay.modules.cheque.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Builder
@Entity
@Table(name = "gms_cheque")
@AllArgsConstructor
@RequiredArgsConstructor
public class Cheque
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "owner_email")
    private String ownerEmail;

    @Column(name = "cheque_amount")
    private BigDecimal chequeAmount;

    @Column(name = "estimated_amount")
    private String estimatedAmount;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "cheque_number")
    private String chequeNumber;

    @Column(name = "routing_number")
    private String routingNumber;

    @Column(name = "bank")
    private String bank;

    @Column(name = "file_uuid")
    private String fileUUID;

    @Column(name = "currency")
    private String currency;

    @Column(name = "status")
    private String status;
}
