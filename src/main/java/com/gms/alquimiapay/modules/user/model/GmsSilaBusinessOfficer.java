package com.gms.alquimiapay.modules.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_sila_business_officer")
@Getter
@Setter
public class GmsSilaBusinessOfficer
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_id")
    private Long businessId;

    @Column(name = "officer_email")
    private String email;

    @Column(name = "officer_handle")
    private String officerHandle;

    @Column(name = "officer_role")
    private String officerRole;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "linked_at")
    private String linkedAt;

    @Column(name = "unlinked_at")
    private String unlinkedAt;

    @Column(name = "status")
    private String status;

    @Column(name = "is_certified")
    private boolean isCertified;

    @Column(name = "certified_at")
    private String certifiedAt;
}
