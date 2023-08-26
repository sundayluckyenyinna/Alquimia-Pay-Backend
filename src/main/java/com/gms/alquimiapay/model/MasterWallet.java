package com.gms.alquimiapay.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_master_wallet")
@Getter
@Setter
public class MasterWallet
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "wallet_id")
    private String walletId;

    @Column(name = "status")
    private String status;

    @Column(name = "vendor")
    private String vendor;
}
