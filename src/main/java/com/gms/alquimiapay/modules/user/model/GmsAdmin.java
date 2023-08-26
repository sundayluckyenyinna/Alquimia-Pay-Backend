package com.gms.alquimiapay.modules.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
public class GmsAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;

    @Column(name = "registration_id")
    private Integer registrationId;

    @Column(name = "login_count")
    private Integer loginCount;

    @Column(name = "login_status")
    private String loginStatus;

    @Column(name = "status")
    private String status;
}
