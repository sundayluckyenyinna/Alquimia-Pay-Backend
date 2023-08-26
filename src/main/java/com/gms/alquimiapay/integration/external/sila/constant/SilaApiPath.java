package com.gms.alquimiapay.integration.external.sila.constant;

public class SilaApiPath
{
    // BASE PATH
    public static final String IDENTITY_BASE = "/identity";


    // USER-IDENTITY
    public static final String USER_CHECK_HANDLE = "/check_handle";
    public static final String REQUEST_KYC = "/request_kyc";
    public static final String CHECK_KYC = "/check_kyc";
    public static final String KYC_DOC_UPLOAD = "/documents";

    // REGISTRATION
    public static final String USER_REGISTRATION = "/register";


    // UTILITY
    public static final String SILA_BUSINESS_TYPES = "/get_business_types";
    public static final String SILA_BUSINESS_CATEGORIES = "/get_naics_categories";
    public static final String SILA_DOCUMENT_TYPES = "/document_types";
    public static final String SILA_BUSINESS_ROLES = "/get_business_roles";

    // BUSINESS MEMBERS
    public static final String LINK_BUSINESS_MEMBER = "/link_business_member";
    public static final String UNLINK_BUSINESS_MEMBER = "/unlink_business_member";
    public static final String GET_ENTITY = "/get_entity";
    public static final String CERTIFY_BENEFICIAL_OWNER = "/certify_beneficial_owner";
    public static final String CERTIFY_BUSINESS = "/certify_business";


    // ################ Wallet ####################### //
    public static final String REGISTER_WALLET = "/register_wallet";
    public static final String GET_SINGLE_WALLET = "/get_wallet";
    public static final String GET_MANY_WALLET = "/get_wallets";
    public static final String DELETE_WALLET = "/delete_wallet";
    public static final String UPDATE_WALLET = "/update_wallet";
}
