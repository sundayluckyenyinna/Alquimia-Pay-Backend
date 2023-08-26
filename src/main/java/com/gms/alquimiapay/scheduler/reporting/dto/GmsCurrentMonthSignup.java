package com.gms.alquimiapay.scheduler.reporting.dto;

import com.gms.alquimiapay.modules.report.annotation.ExcelHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GmsCurrentMonthSignup
{
    @ExcelHeader(name = "S/N")
    private String sn;

    @ExcelHeader(name = "USER_ID")
    private String userId;

    @ExcelHeader(name = "USER_TYPE")
    private String userType;

    @ExcelHeader(name = "FIRST_NAME")
    private String firstName;

    @ExcelHeader(name = "LAST_NAME")
    private String lastName;

    @ExcelHeader(name = "MIDDLE_NAME")
    private String middleName;

    @ExcelHeader(name = "USERNAME")
    private String username;

    @ExcelHeader(name = "EMAIL_ADDRESS")
    private String emailAddress;

    @ExcelHeader(name = "EMAIL_VERIFIED_AT")
    private String emailVerifiedAt;

    @ExcelHeader(name = "ADDRESS")
    private String address;

    @ExcelHeader(name = "ADDRESS_2")
    private String address2;

    @ExcelHeader(name = "MOBILE_NUMBER")
    private String mobileNumber;

    @ExcelHeader(name = "COUNTRY")
    private String country;

    @ExcelHeader(name = "BUSINESS_NAME")
    private String businessName;

    @ExcelHeader(name = "CHANNEL")
    private String channel;

    @ExcelHeader(name = "STATUS")
    private String status;

    @ExcelHeader(name = "BUSINESS_TYPE")
    private String businessType;

    @ExcelHeader(name = "BUSINESS_CATEGORY_CODE")
    private String businessCategoryCode;

    @ExcelHeader(name = "BUSINESS_NICKNAME")
    private String businessNickName;

    @ExcelHeader(name = "BUSINESS_WEBSITE")
    private String businessWebsite;

    @ExcelHeader(name = "BUSINESS_ADDRESS")
    private String businessAddress;

    @ExcelHeader(name = "BUSINESS_EMAIL")
    private String businessEmail;

    @ExcelHeader(name = "BUSINESS_PHONE")
    private String businessPhone;

    @ExcelHeader(name = "WALLET_ID")
    private String walletId;

    @ExcelHeader(name = "WIRE_VIRTUAL_ACCOUNT_NUMBER")
    private String wireVirtualAccountNumber;

}
