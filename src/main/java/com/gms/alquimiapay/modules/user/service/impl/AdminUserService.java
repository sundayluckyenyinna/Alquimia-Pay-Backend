package com.gms.alquimiapay.modules.user.service.impl;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.UserStatus;
import com.gms.alquimiapay.modules.audit.service.IAdminAuditService;
import com.gms.alquimiapay.modules.user.model.GmsAdmin;
import com.gms.alquimiapay.modules.user.payload.admin.request.AdminUserRequestPayload;
import com.gms.alquimiapay.modules.user.repository.IGmsAdminRepository;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.modules.user.service.IAdminUserService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.util.JwtUtil;
import com.gms.alquimiapay.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService implements IAdminUserService {

    private final IUserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final IGmsAdminRepository adminRepository;
    private final MessageProvider messageProvider;
    private final IAdminAuditService adminAuditService;

    @Value("${security.admin.allowed-login-count}")
    private Integer allowedLoginCount;


    @SuppressWarnings("DuplicatedCode")
    @Override
    public BaseResponse processAdminUserLogin(AdminUserRequestPayload requestPayload) {
        BaseResponse response = new BaseResponse();
        String code = ResponseCode.SYSTEM_ERROR;
        String operationMessage = "Admin Login";
        GmsAdmin admin = null;
        try {
            // Check if the admin exist in the database
            admin = adminRepository.findByUsername(requestPayload.getUsername());
            if (admin == null) {
                code = ResponseCode.RECORD_NOT_FOUND;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, new GmsAdmin());
                return response;
            }

            // Check if the admin is already logged in
            if (admin.getLoginStatus().equalsIgnoreCase(UserStatus.LOGGED_IN.name())) {
                code = ResponseCode.ADMIN_ALREADY_LOGGED_IN;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, admin);
                return response;
            }

            // Check if the account is locked.
            if (admin.getStatus().equalsIgnoreCase(UserStatus.LOCKED.name())) {
                code = ResponseCode.ACCOUNT_LOCKED;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, admin);
                return response;
            }

            // Check that the account must be active
            if (!admin.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.name())) {
                code = ResponseCode.ACCOUNT_UNVERIFIED;
                response.setResponseCode(code);
                response.setResponseMessage(String.format("Could not find active account for username: %s", requestPayload.getUsername()));
                adminAuditService.saveAudit(response, code, operationMessage, admin);
                return response;
            }

            // Check for the validity of the password.
            String incomingPassword = requestPayload.getPassword();
            boolean isPasswordMatch = passwordUtil.isPasswordMatch(incomingPassword, admin.getPassword());
            if (!isPasswordMatch) {
                int newLoginCount = admin.getLoginCount() + 1;
                if (newLoginCount >= allowedLoginCount) {
                    // Lock the account
                    admin.setStatus(UserStatus.LOCKED.name());
                    admin.setLoginStatus("LOGIN_LOCKED");
                    admin.setStatus(UserStatus.LOCKED.name());
                    adminRepository.saveAndFlush(admin);
                }
                admin.setLoginCount(newLoginCount);
                adminRepository.saveAndFlush(admin);
                code = ResponseCode.INVALID_PASSWORD;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, admin);
                return response;
            }

            // All validation passed. Log in the user.
            admin.setLoginStatus(UserStatus.LOGGED_IN.name());
            adminRepository.saveAndFlush(admin);

            code = ResponseCode.SUCCESS;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception occurred while trying to login the admin user: {}", e.getMessage());

            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin == null ? new GmsAdmin() : admin);
            return response;
        }

    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public BaseResponse processAdminUserLogout(AdminUserRequestPayload requestPayload) {
        BaseResponse response = new BaseResponse();
        String code = ResponseCode.SYSTEM_ERROR;
        String operationMessage = "Admin logout";

        GmsAdmin admin = null;
        try {
            // Check if the admin exist in the database
            admin = adminRepository.findByUsername(requestPayload.getUsername());
            if (admin == null) {
                code = ResponseCode.RECORD_NOT_FOUND;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, new GmsAdmin());
                return response;
            }

            // Check if the admin is already logged out
            if (admin.getStatus().equalsIgnoreCase(UserStatus.LOGGED_OUT.name())) {
                code = ResponseCode.ADMIN_ALREADY_LOGGED_OUT;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, admin);
                return response;
            }

            // Check for the validity of the password.
            String incomingPassword = requestPayload.getPassword();
            boolean isPasswordMatch = passwordUtil.isPasswordMatch(incomingPassword, admin.getPassword());
            if (!isPasswordMatch) {
                int newLoginCount = admin.getLoginCount() + 1;
                if (newLoginCount >= allowedLoginCount) {
                    // Lock the account
                    admin.setStatus(UserStatus.LOCKED.name());
                    admin.setLoginStatus("LOGIN_LOCKED");
                    adminRepository.saveAndFlush(admin);
                }
                code = ResponseCode.INVALID_PASSWORD;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, admin);
                return response;
            }

            // All validation passed. Log out the user.
            admin.setLoginStatus(UserStatus.LOGGED_OUT.name());
            adminRepository.saveAndFlush(admin);

            code = ResponseCode.SUCCESS;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception occurred while trying to logout the admin user: {}", e.getMessage());

            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin == null ? new GmsAdmin() : admin);
            return response;
        }
    }


    @SuppressWarnings("DuplicatedCode")
    @Override
    public BaseResponse processAdminUserSessionContinuation(String username, String password) {
        BaseResponse response = new BaseResponse();
        String code = ResponseCode.SYSTEM_ERROR;
        String operationMessage = "Admin session continuation check";

        GmsAdmin admin = null;
        try {
            // Check if the admin exist in the database
            admin = adminRepository.findByUsername(username);
            if (admin == null) {
                code = ResponseCode.RECORD_NOT_FOUND;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, new GmsAdmin());
                return response;
            }

            // Check if the account is locked.
            if (admin.getStatus().equalsIgnoreCase(UserStatus.LOCKED.name())) {
                code = ResponseCode.ACCOUNT_LOCKED;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, admin);
                return response;
            }

            // Check that the account must be active
            if (!admin.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.name())) {
                code = ResponseCode.ACCOUNT_UNVERIFIED;
                response.setResponseCode(code);
                response.setResponseMessage(String.format("Could not find active account for username: %s", username));
                adminAuditService.saveAudit(response, code, operationMessage, admin);
                return response;
            }

            // Check that he is logged in.
            if (!admin.getLoginStatus().equalsIgnoreCase(UserStatus.LOGGED_IN.name())) {
                code = ResponseCode.ADMIN_NOT_LOGGED_IN;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, admin);
                return response;
            }

            // Check for the validity of the password.
            boolean isPasswordMatch = passwordUtil.isPasswordMatch(password, admin.getPassword());
            if (!isPasswordMatch) {
                int newLoginCount = admin.getLoginCount() + 1;
                if (newLoginCount >= allowedLoginCount) {
                    // Lock the account
                    admin.setStatus(UserStatus.LOCKED.name());
                    admin.setLoginStatus("LOGIN_LOCKED");
                    admin.setStatus(UserStatus.LOCKED.name());
                    adminRepository.saveAndFlush(admin);
                }
                admin.setLoginCount(newLoginCount);
                adminRepository.saveAndFlush(admin);
                code = ResponseCode.INVALID_PASSWORD;
                response.setResponseCode(code);
                response.setResponseMessage(messageProvider.getMessage(code));
                adminAuditService.saveAudit(response, code, operationMessage, admin);
                return response;
            }

            code = ResponseCode.SUCCESS;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            response.setOtherDetails(admin);
            adminAuditService.saveAudit(response, code, operationMessage, admin);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception occurred while trying to login the admin user: {}", e.getMessage());

            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            adminAuditService.saveAudit(response, code, operationMessage, admin == null ? new GmsAdmin() : admin);
            return response;
        }
    }
}
