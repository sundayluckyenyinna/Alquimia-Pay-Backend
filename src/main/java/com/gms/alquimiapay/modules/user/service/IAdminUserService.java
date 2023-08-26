package com.gms.alquimiapay.modules.user.service;

import com.gms.alquimiapay.modules.user.payload.admin.request.AdminUserRequestPayload;
import com.gms.alquimiapay.payload.BaseResponse;

public interface IAdminUserService {

    BaseResponse processAdminUserLogin(AdminUserRequestPayload requestPayload);

    BaseResponse processAdminUserLogout(AdminUserRequestPayload requestPayload);

    BaseResponse processAdminUserSessionContinuation(String username, String password);
}
