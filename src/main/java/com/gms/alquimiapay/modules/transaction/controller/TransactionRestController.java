package com.gms.alquimiapay.modules.transaction.controller;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.transaction.payload.request.CashTransferRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.request.CreateBeneficiaryRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.request.TransactionFeeRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.request.TransactionHistoryRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.response.*;
import com.gms.alquimiapay.modules.transaction.service.ITransactionService;
import com.gms.alquimiapay.validation.GenericValidator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.gms.alquimiapay.modules.transaction.constant.TransactionApiPath.*;

@RestController
@RequestMapping(TRANSACTION_BASE_API_PATH)
@Api(tags = "Transaction Service", description = "Provides all functionalities for transactions")
public class TransactionRestController
{
    private final GenericValidator genericValidator;

    private final ITransactionService transactionService;


    @Autowired
    public TransactionRestController(GenericValidator genericValidator, ITransactionService transactionService) {
        this.genericValidator = genericValidator;
        this.transactionService = transactionService;
    }

    @Operation(summary = "Returns the transaction fee associated to a given transaction type", description = "Returns the transaction fee associated to a given transaction type")
    @PostMapping(value = TRANSACTION_FEE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionFeeResponsePayload> handleTransactionFeeRequest(@RequestBody TransactionFeeRequestPayload requestPayload){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(transactionService.processTransactionFeeRequest(requestPayload));
    }

    @Operation(summary = "Post cash transfer from user wallet to destination account", description = "Post cash transfer from user wallet to destination account")
    @PostMapping(value = TRANSFER_PAYMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CashTransferResponsePayload> handleCashTransfer(@RequestBody CashTransferRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(transactionService.processCashTransferRequest(requestPayload, authToken));
    }

    @Operation(summary = "Fetch all transaction histories associated with a user", description = "Fetch all transaction histories associated with a user")
    @PostMapping(value = TRANSACTION_HISTORY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionHistoryResponsePayload> handleTransactionHistory(@RequestBody TransactionHistoryRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(transactionService.processTransactionHistoryRequest(requestPayload, authToken));
    }

    @Operation(summary = "Fetch the status of a transaction by virtue of its externalReference", description = "Fetch the status of a transaction by virtue of its externalReference")
    @GetMapping(value = TRANSACTION_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionStatusResponsePayload> handleTransactionStatus(@RequestParam("externalReference") String reference){
        return ResponseEntity.ok(transactionService.processTransactionStatus(reference));
    }

    @Operation(summary = "Create a beneficiary for a customer", description = "Fetch the status of a transaction by virtue of its externalReference")
    @PostMapping(value = CUSTOMER_BENEFICIARY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateBeneficiaryResponsePayload> handleCreateBeneficiary(@RequestBody CreateBeneficiaryRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(transactionService.processCustomerBeneficiaryCreation(requestPayload, authToken));
    }

    @Operation(summary = "Fetch all the customer beneficiaries", description = "Fetch all the customer beneficiaries")
    @GetMapping(value = CUSTOMER_BENEFICIARY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerBeneficiaryListResponsePayload> handleGetAllBeneficiaries(@RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        return ResponseEntity.ok(transactionService.processGetCustomerBeneficiaryList(authToken));
    }
}
