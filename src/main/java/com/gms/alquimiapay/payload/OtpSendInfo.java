package com.gms.alquimiapay.payload;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OtpSendInfo
{
    private String otpSent;
    private LocalDateTime createdDateTime;
    private LocalDateTime expirationDateTime;
    private String recipientEmail;

    public static OtpSendInfo getInstance(){
        return new OtpSendInfo();
    }
}
