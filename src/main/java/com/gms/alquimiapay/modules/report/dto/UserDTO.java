package com.gms.alquimiapay.modules.report.dto;

import lombok.Data;


@Data
public class UserDTO
{
    private String userId;

    private String userType;

    private String firstName;

    private String lastName;

    private String middleName;

    private String username;

    private String emailAddress;

    private String emailVerifiedAt;

    private String address;

    private String address2;

    private String mobileNumber;

    private String city;

    private String state;

    private String localGovernment;

    private String country;

    private String zipCode;

    private String lastLoginDate;

    private String createdAt;

    private String updatedAt;

    private String verifySubmitStatus;

    private Boolean isVerified;

    private String businessName;

    private String channel;

    private String status;

    private String ssn;

    private String addressAlias;

    private String contactAlias;

    private String employerIdentificationNumber;

    private String businessType;

    private String businessTypeUUID;

    private String businessCategoryCode;

    private String businessNickName;

    private String businessWebsite;

    private String businessEin;

    private String businessIncDate;

    private String businessAddress;

    private String businessEmail;

    private String businessPhone;

    private String businessRegState;

    private String walletCreationFailureReason;

    private String virtualAccountCreationFailureReason;
}
