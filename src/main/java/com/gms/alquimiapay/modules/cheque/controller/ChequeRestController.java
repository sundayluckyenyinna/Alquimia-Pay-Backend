package com.gms.alquimiapay.modules.cheque.controller;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.cheque.payload.SingleChequeResponsePayload;
import com.gms.alquimiapay.modules.cheque.payload.data.ChequeListResponsePayload;
import com.gms.alquimiapay.modules.cheque.payload.request.ChequeDepositRequestPayload;
import com.gms.alquimiapay.modules.cheque.payload.request.ChequeListRequestPayload;
import com.gms.alquimiapay.modules.cheque.payload.request.SubmitDepositChequeRequestPayload;
import com.gms.alquimiapay.modules.cheque.payload.response.SubmitChequeDepositResponsePayload;
import com.gms.alquimiapay.modules.cheque.service.IChequeDepositService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.validation.GenericValidator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.gms.alquimiapay.modules.cheque.constant.ChequeApiPath.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = CHECK_DEPOSIT_BASE_URL)
@Api(tags = "Cheque Management")
public class ChequeRestController {

    private final GenericValidator validator;
    private final IChequeDepositService chequeDepositService;

    // CUSTOMER | BUSINESS USER
    @Operation(summary = "Submit a cheque for processing", description = "Submit a cheque for approval")
    @PostMapping(value = SUBMIT_CHECK_DEPOSIT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SubmitChequeDepositResponsePayload> handleChequeSubmission(@RequestBody SubmitDepositChequeRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        validator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(chequeDepositService.processChequeSubmission(authToken, requestPayload));
    }

    @Operation(summary = "Retrieves a list of all customer's cheques objects stored in the system by virtue of the start and end date", description = "Retrieves a list of all customer's cheques objects stored in the system by virtue of the start and end date")
    @GetMapping(value = USER_CHEQUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChequeListResponsePayload> handleChequeListRequestForUser(
            @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken,
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", required = false) String status
    ){
        return ResponseEntity.ok(chequeDepositService.processChequeListForUser(authToken, startDate, endDate, status));
    }


    // ADMIN
    @Operation(summary = "Submits a cheque request for approval", description = "Submit a cheque request for approval")
    @PostMapping(value = APPROVE_CHECK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleChequeApprovalRequest(@RequestBody ChequeDepositRequestPayload requestPayload){
        validator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(chequeDepositService.processChequeDepositVerification(requestPayload));
    }

    @Operation(summary = "Submits a cheque request for rejection", description = "Submits a cheque request for rejection")
    @PostMapping(value = REJECT_CHECK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleChequeRejectionRequest(@RequestBody ChequeDepositRequestPayload requestPayload){
        validator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(chequeDepositService.processChequeDepositRejection(requestPayload));
    }

    @Operation(summary = "Retrieves all checks between a start date to an end date", description = "Retrieves all checks between a start date to an end date. \nNote: If the end date is not provided, the system returns a list of cheques for the day equivalent t the start date.")
    @PostMapping(value = LIST_CHEQUES, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChequeListResponsePayload> handleChequeListRequest(@RequestBody ChequeListRequestPayload requestPayload){
        validator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(chequeDepositService.processChequeListForAdmin(requestPayload));
    }


    // GENERAL
    @Operation(summary = "Retrieves a single cheque object stored in the system by virtue of the chequeId", description = "Retrieves a single cheque object stored in the system by virtue of the chequeId")
    @GetMapping(value = SINGLE_CHEQUE_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SingleChequeResponsePayload> handleSingleChequeRequest(@RequestParam("chequeId") String chequeId){
        return ResponseEntity.ok(chequeDepositService.processSingleChequeById(chequeId));
    }
}
