package com.gms.alquimiapay.webhook.controller;

import com.gms.alquimiapay.webhook.service.WebhookNotificationServiceHandler;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/circle/webhook")
@Api(tags = "Circle Webhook Service", description = "Webhook consumable by Circle")
public class CircleWebhookRestController {

    private final WebhookNotificationServiceHandler webhookNotificationServiceHandler;

    @Autowired
    public CircleWebhookRestController(WebhookNotificationServiceHandler webhookNotificationServiceHandler) {
        this.webhookNotificationServiceHandler = webhookNotificationServiceHandler;
    }


    @RequestMapping(method = {RequestMethod.HEAD})
    public ResponseEntity<String> handleWebhookHeadRequest(){
        log.info("Head request received...");
        return ResponseEntity.ok("Head request confirmed!");
    }

    @PostMapping(consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> handleWebhookPostRequest(@RequestBody String body, @RequestParam Map<String, String> params){

        // Log the notification object.
        log.info("Notification object: {}", body);

        // Call the service to save the notification object for further processing.
        String webhookHandlerResponse = webhookNotificationServiceHandler.saveNotificationModelForUpdate(body);
        log.info("WebhookServiceHandlerResponse: {}", webhookHandlerResponse);

        // Return success response to calling application.
        return ResponseEntity.ok(webhookHandlerResponse);
    }

}
