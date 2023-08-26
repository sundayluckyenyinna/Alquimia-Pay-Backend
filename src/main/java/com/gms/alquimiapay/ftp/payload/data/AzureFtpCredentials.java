package com.gms.alquimiapay.ftp.payload.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AzureFtpCredentials
{
    private String accountName;
    private String endpoint;
    private String accountKey;
}
