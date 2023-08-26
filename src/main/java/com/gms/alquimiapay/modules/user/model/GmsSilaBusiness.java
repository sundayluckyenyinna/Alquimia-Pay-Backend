package com.gms.alquimiapay.modules.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_sila_business")
@Getter
@Setter
public class GmsSilaBusiness
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "business_email")
    private String businessEmail;

    @Column(name = "business_website")
    private String businessWebsite;

    @Column(name = "business_handle")
    private String businessHandle;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "requires_certification")
    private boolean requiresCertification;

    @Column(name = "is_certified")
    private boolean isCertified;

    @Column(name = "certified_at")
    private String certifiedAt;
}
