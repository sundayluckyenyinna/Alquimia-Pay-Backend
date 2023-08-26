package com.gms.alquimiapay.modules.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_user_device")
@Setter
@Getter
public class GmsUserDevice
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "owner_email")
    private String ownerEmail;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "is_linked")
    private boolean isLinked;

    @Column(name = "linking_otp_id")
    private Long linkingOtpId;
}
