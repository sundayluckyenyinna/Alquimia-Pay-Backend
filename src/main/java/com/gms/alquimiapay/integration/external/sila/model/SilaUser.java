package com.gms.alquimiapay.integration.external.sila.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_sila_user")
@Getter
@Setter
public class SilaUser
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gms_user_id")
    private String gmsUserId;

    @Column(name = "gms_user_email")
    private String gmsUserEmail;

    @Column(name = "sila_user_handle")
    private String silaUserHandle;
}
