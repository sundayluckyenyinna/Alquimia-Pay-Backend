package com.gms.alquimiapay.modules.user.controller;

import com.gms.alquimiapay.modules.user.constants.UserApiPaths;
import com.gms.alquimiapay.modules.user.payload.admin.request.AdminUserRequestPayload;
import com.gms.alquimiapay.modules.user.service.IAdminUserService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.validation.GenericValidator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(UserApiPaths.USER_BASE_URL)
@Api(tags = "Admin Session Management", description = "Manages the session associated with the admin user")
public class AdminUserController
{

        private final IAdminUserService adminUserService;
        private final GenericValidator validator;

        @Operation(summary = "Handle admin login request", description = "Handle admin login request")
        @PostMapping(value = UserApiPaths.ADMIN_LOGIN)
        public ResponseEntity<BaseResponse> handleAdminUserLogin(@RequestBody AdminUserRequestPayload requestPayload){
                validator.doModelValidationThrowException(requestPayload);
                return ResponseEntity.ok(adminUserService.processAdminUserLogin(requestPayload));
        }

        @Operation(summary = "Handle admin logout request", description = "Handle admin logout request")
        @PostMapping(value = UserApiPaths.ADMIN_LOGOUT)
        public ResponseEntity<BaseResponse> handleAdminUserLogout(@RequestBody AdminUserRequestPayload requestPayload){
                validator.doModelValidationThrowException(requestPayload);
                return ResponseEntity.ok(adminUserService.processAdminUserLogout(requestPayload));
        }
}
