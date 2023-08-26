package com.gms.alquimiapay.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_param")
@Getter
@Setter
public class GmsParam
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "param_key")
    private String paramKey;

    @Column(name = "param_value", columnDefinition = "text")
    private String paramValue;

    @Column(name = "param_desc", columnDefinition = "text")
    private String paramDesc;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "expiration_at")
    private String expirationAt;

    @Column(name = "other_info_logs", columnDefinition = "text")
    private String otherInfoLogs;
}
