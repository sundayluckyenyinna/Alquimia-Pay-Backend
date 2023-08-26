package com.gms.alquimiapay.modules.account.service;

import com.gms.alquimiapay.payload.BaseResponse;
import org.springframework.stereotype.Service;

@Service
public interface IAccountService {

    BaseResponse processVirtualAccountCreation(String userEmail);
}
