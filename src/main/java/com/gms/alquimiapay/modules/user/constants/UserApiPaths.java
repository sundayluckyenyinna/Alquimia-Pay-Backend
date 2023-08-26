package com.gms.alquimiapay.modules.user.constants;

public interface UserApiPaths
{

    // ON-BOARDING
    String USER_BASE_URL = "/user";
    String USER_SIGN_UP = "/signup";
    String VERIFY_SIGNUP_OTP = "signup/otp/verification";
    String USER_LOGIN = "/login";
    String USER_LOGOUT = "/logout";
    String FORGOT_PASSWORD_OTP_REQUEST = "/security/forgot-password";
    String FORGOT_PASSWORD_OTP_VERIFY = "/security/forgot-password/verify-otp";
    String RESET_PASSWORD = "/security/password/change";
    String NEW_DEVICE_OTP_VERIFICATION = "/security/devices/new-linking";

    // IDENTITY
    String USER_IDENTITY_BASE = "/identity";
    String CHECK_USER_HANDLE = "/check-handle";
    String INDIVIDUAL_USER_REG = "/kyc/individual";
    String BUSINESS_USER_REG = "/kyc/business";
    String UPLOAD_KYC_DOCUMENT = "/kyc/document-upload";
    String REQUEST_KYC = "/kyc/request";
    String CHECK_KYC = "/kyc/check";
    String USER_DETAILS = "/details";
    String PIN_CHANGE = "/security/pin-update";
    String PIN_RESET_OTP_REQUEST = "/security/pin-reset/otp";
    String PIN_RESET_OTP_VERIFICATION = "/security/pin-reset/otp/verify";

    // BUSINESS
    String LINK_ADMIN_OFFICER = "/kyc/business-officer/link/admin";
    String LINK_CONTROLLING_OFFICER = "/kyc/business-officer/link/controlling-officer";
    String LINK_BENEFICIAL_OWNER_OFFICER = "/kyc/business-officer/link/beneficial-owner";
    String UNLINK_BUSINESS_MEMBER = "/kyc/business-officer/unlink";
    String CERTIFY_BENEFICIAL_OWNER = "/kyc/business-officer/beneficial-owner/certification";
    String CERTIFY_BUSINESS = "/kyc/business/certification";

    // ADMIN
    String ADMIN_LOGIN = "/admin/login";
    String ADMIN_LOGOUT = "/admin/logout";
}
