package com.gms.alquimiapay.modules.kyc.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_user_verification")
@Getter
@Setter
public class UserKycVerification
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email")
    private String  userEmail;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "status")
    private String status;

    @Column(name = "kyc_level")
    private String kycLevel;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "internal_reference")
    private String internalReference;

    @Column(name = "kyc_tier")
    private String kycTier;

    @Column(name = "logs")
    private String logs;
}
