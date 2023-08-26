package com.gms.alquimiapay.webhook.controller;

import com.gms.alquimiapay.modules.kyc.repository.UserKycVerificationRepository;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.webhook.dto.KycUpdateEventDTO;
import com.gms.alquimiapay.webhook.service.KycUpdateWebhookService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@Api(tags = "KYC Webhook Service", hidden = true, description = "This webhook is configured for Sila webservice as a call-back service after a KYC update.")
public class SilaWebhookController
{
    @Autowired
    private UserKycVerificationRepository kycVerificationRepository;
    @Autowired
    private KycUpdateWebhookService kycUpdateWebhookService;

    @PostMapping(value = "/kyc-status-update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleKycStatusUpdate(@RequestBody KycUpdateEventDTO requestDTO){
        return ResponseEntity.ok(kycUpdateWebhookService.processKycUpdate(requestDTO));
    }
}
