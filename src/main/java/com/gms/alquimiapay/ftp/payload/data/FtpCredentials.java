package com.gms.alquimiapay.ftp.payload.data;

import lombok.Data;

@Data
public class FtpCredentials
{
    private String serverHostName;
    private Integer serverPort;
    private String username;
    private String password;
}
