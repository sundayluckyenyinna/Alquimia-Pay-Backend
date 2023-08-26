package com.gms.alquimiapay.modules.user.model;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.user.constants.UserType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_users")
@Getter
@Setter
public class GmsUser
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    private String username;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "email_verified_at")
    private String emailVerifiedAt;

    @Column(name = "password")
    private String password;

    @Column(name = "address")
    private String address;

    @Column(name = "address2")
    private String address2;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "local_govt_area")
    private String localGovernment;

    @Column(name = "country")
    private String country;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "last_login_date")
    private String lastLoginDate;

    @Column(name = "deleted_at")
    private String deletedAt;

    @Column(name = "auth_token")
    private String authToken;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "verify_submit_status")
    private String verifySubmitStatus;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "locale")
    private String locale;

    @Column(name = "user_role_id")
    private Long userRoleId;

    @Column(name = "photo_link")
    private String photoLink;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "geo_location")
    private String geoLocation;

    @Column(name = "login_attempt")
    private Integer loginAttempt;

    @Column(name = "gender")
    private String gender;

    @Column(name = "channel")
    private String channel;

    @Column(name = "auth_token_created_date")
    private String authTokenCreatedDate;

    @Column(name = "auth_token_expiration_date")
    private String authTokenExpirationDate;

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_created_date")
    private String otpCreatedDate;

    @Column(name = "otp_exp_date")
    private String otpExpDate;

    @Column(name = "is_otp_verified")
    private Boolean isOtpVerified;

    @Column(name = "status")
    private String status;

    @Column(name = "ssn")
    private String ssn;

    @Column(name = "address_alias")
    private String addressAlias;

    @Column(name = "contact_alias")
    private String contactAlias;

    @Column(name = "employer_ein")
    private String employerIdentificationNumber;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "business_type_uuid")
    private String businessTypeUUID;

    @Column(name = "naics_code")
    private String businessCategoryCode;

    @Column(name = "business_nick_name")
    private String businessNickName;

    @Column(name = "business_website")
    private String businessWebsite;

    @Column(name = "transaction_pin")
    private String transactionPin;

    @Column(name = "pin_created_at")
    private String pinCreatedAt;

    @Column(name = "pin_updated_at")
    private String pinUpdatedAt;

    @Column(name = "pin_created_by")
    private String pinCreatedBy;

    @Column(name = "pin_updated_by")
    private String pinUpdatedBy;

    @Column(name = "business_ein")
    private String businessEin;

    @Column(name = "business_inc_date")
    private String businessIncDate;

    @Column(name = "business_address")
    private String businessAddress;

    @Column(name = "business_email")
    private String businessEmail;

    @Column(name = "business_phone")
    private String businessPhone;

    @Column(name = "business_reg_state")
    private String businessRegState;

    @Column(name = "is_registered_with_sila")
    private boolean isRegisteredWithSila;

    @Column(name = "wallet_creation_failure_reason", columnDefinition = "text")
    private String walletCreationFailureReason;

    @Column(name = "virtual_account_creation_failure_reason", columnDefinition = "text")
    private String virtualAccountCreationFailureReason;

    public String getName(){
        if(this.userType.equalsIgnoreCase(UserType.BUSINESS.name()))
            return this.getBusinessName();
        else if(this.userType.equalsIgnoreCase(UserType.INDIVIDUAL.name()))
            return String.join(StringValues.SINGLE_EMPTY_SPACE, this.getLastName(), this.getFirstName(), this.getMiddleName());
        else
            return StringValues.EMPTY_STRING;
    }
}
