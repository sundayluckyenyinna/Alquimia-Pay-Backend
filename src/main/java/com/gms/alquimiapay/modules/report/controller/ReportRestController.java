package com.gms.alquimiapay.modules.report.controller;

import com.gms.alquimiapay.modules.report.payload.request.ReportRequestPayload;
import com.gms.alquimiapay.modules.report.payload.response.AlquimiaRemittanceResponsePayload;
import com.gms.alquimiapay.modules.report.payload.response.DepositsResponsePayload;
import com.gms.alquimiapay.modules.report.payload.response.TransactionEntriesResponsePayload;
import com.gms.alquimiapay.modules.report.payload.response.UsersResponsePayload;
import com.gms.alquimiapay.modules.report.service.IReportService;
import com.gms.alquimiapay.validation.GenericValidator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gms.alquimiapay.modules.report.constants.ReportApiPath.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = BASE_PATH)
@Api(tags = "Report Service", description = "RESTful API service for reports")
public class ReportRestController
{
    private final IReportService reportService;
    private final GenericValidator validator;

    @Operation(summary = "Fetch all Alquimia Remittance in the system between start and date dates", description = "Fetch all Alquimia Remittance in the system between start and date dates")
    @PostMapping(value = GET_ALQUIMIA_REMITTANCE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlquimiaRemittanceResponsePayload> handleAlquimiaRemittanceRequest(@RequestBody ReportRequestPayload requestPayload){
        validator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(reportService.getAlquimiaRemittance(requestPayload));
    }

    @Operation(summary = "Fetch all account deposit in the system between start and date dates", description = "Fetch all account deposit in the system between start and date dates")
    @PostMapping(value = GET_DEPOSITS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepositsResponsePayload> handleDepositsRequest(@RequestBody ReportRequestPayload requestPayload){
        validator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(reportService.getDeposits(requestPayload));
    }

    @Operation(summary = "Fetch all transaction entries in the system between start and date dates", description = "Fetch all transaction entries in the system between start and date dates")
    @PostMapping(value = GET_TRANSACTION_ENTRIES_FOR_ALL_USERS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionEntriesResponsePayload> handleGetTransactionEntriesRequest(@RequestBody ReportRequestPayload requestPayload){
        validator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(reportService.getTransactionEntries(requestPayload));
    }

    @Operation(summary = "Fetch all users in the system between start and date dates", description = "Fetch all users in the system between start and date dates")
    @PostMapping(value = GET_USERS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersResponsePayload> handleGetUsersRequest(@RequestBody ReportRequestPayload requestPayload){
        validator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(reportService.getSignupUsers(requestPayload));
    }
}
