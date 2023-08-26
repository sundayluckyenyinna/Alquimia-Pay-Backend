package com.gms.alquimiapay.modules.wallet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_wallet_cache")
@Getter
@Setter
public class GmsWalletCache
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "wallet_name")
    private String walletName;

    @Column(name = "wallet_id")
    private String walletId;

    @SerializedName("is_default")
    private Boolean isDefault;

    @Column(name = "owner_email")
    private String ownerEmail;

    @Column(name = "customer_full_name")
    private String ownerFullName;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "is_whitelisted")
    private Boolean isWhiteListed;

    @Column(name = "available_balance", columnDefinition = "text")
    private String availableBalance;

    @Column(name = "pending_balance", columnDefinition = "text")
    private String pendingBalance;

    @Column(name = "blockchain_address", columnDefinition = "text")
    private String blockchainAddress;

    @Column(name = "blockchain_network")
    private String blockchainNetwork;

    @Column(name = "status")
    private String status;
}
