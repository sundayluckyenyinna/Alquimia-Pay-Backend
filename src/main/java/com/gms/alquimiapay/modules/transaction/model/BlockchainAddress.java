package com.gms.alquimiapay.modules.transaction.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "blockchain_address")
@Getter
@Setter
public class BlockchainAddress
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "internal_ref", columnDefinition = "text")
    private String internalRef;

    @Column(name = "external_ref")
    private String externalRef;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Column(name = "chain")
    private String chain;

    @Column(name = "address_tag")
    private String addressTag;

    @Column(name = "currency")
    private String currency;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "owner_customer_name")
    private String ownerCustomerName;

    @Column(name = "owner_customer_email")
    private String ownerCustomerEmail;
}
