package com.gms.alquimiapay.modules.user.payload.onboarding.data;

import com.gms.alquimiapay.modules.account.payload.data.WireVirtualAccountData;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponseData
{
    private String userId;
    private String username;
    private String emailAddress;
    private String userType;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    private String businessName;
    private String address;
    private String mobileNumber;
    private String status;
    private String lastLoginDate;
    private String photoLink;
    private String createdDate;
    private String modifiedDate;
    private String deviceId;
    private String geoLocation;
    private String language;
    private String city;
    private String country;
    private String authToken;
    private UserKycData kyc;
    private WalletData wallet;
    private WireVirtualAccountData account;
    private UserLoginResponseData(){}
    public static UserLoginResponseData createInstance(){
        return new UserLoginResponseData();
    }
}
