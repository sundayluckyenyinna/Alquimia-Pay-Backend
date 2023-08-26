package com.gms.alquimiapay.modules.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_user_otp")
@Getter
@Setter
public class GmsUserOtp
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp_type")
    private String otpType;

    @Column(name = "otp_for")
    private String otpFor;

    @Column(name = "otp_owner_email")
    private String otpOwner;

    @Column(name = "otp_value")
    private String otpValue;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "exp_at")
    private String expAt;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "is_verified")
    private boolean isVerified;
}
