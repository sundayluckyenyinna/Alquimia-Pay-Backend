package com.gms.alquimiapay.modules.report.service;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.modules.account.repository.IAccountDepositRepository;
import com.gms.alquimiapay.modules.audit.service.IAdminAuditService;
import com.gms.alquimiapay.modules.report.dto.AccountDepositDTO;
import com.gms.alquimiapay.modules.report.dto.AlquimiaRemittanceDTO;
import com.gms.alquimiapay.modules.report.dto.TransactionEntryDTO;
import com.gms.alquimiapay.modules.report.dto.UserDTO;
import com.gms.alquimiapay.modules.report.payload.data.ReportResponseData;
import com.gms.alquimiapay.modules.report.payload.request.ReportRequestPayload;
import com.gms.alquimiapay.modules.report.payload.response.AlquimiaRemittanceResponsePayload;
import com.gms.alquimiapay.modules.report.payload.response.DepositsResponsePayload;
import com.gms.alquimiapay.modules.report.payload.response.TransactionEntriesResponsePayload;
import com.gms.alquimiapay.modules.report.payload.response.UsersResponsePayload;
import com.gms.alquimiapay.modules.transaction.repository.IGmsRemittanceRepository;
import com.gms.alquimiapay.modules.transaction.repository.ITransactionEntryRepository;
import com.gms.alquimiapay.modules.user.model.GmsAdmin;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.modules.user.service.IAdminUserService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService{

    private final IAdminUserService adminUserService;
    private final IAdminAuditService adminAuditService;
    private final IUserRepository userRepository;
    private final ITransactionEntryRepository transactionEntryRepository;
    private final IGmsRemittanceRepository remittanceRepository;
    private final IAccountDepositRepository accountDepositRepository;
    private final MessageProvider messageProvider;
    private final static Gson JSON = new Gson();


    @Override
    public UsersResponsePayload getSignupUsers(ReportRequestPayload requestPayload){
        UsersResponsePayload responsePayload = new UsersResponsePayload();
        String operationMessage = "Fetch registered users";

        // Validate that the request is coming from the continuation session of the admin.
        BaseResponse adminSessionContinuation = adminUserService.processAdminUserSessionContinuation(requestPayload.getAdminUser(), requestPayload.getAdminPasscode());
        Object adminObject = adminSessionContinuation.getOtherDetails();
        GmsAdmin admin = adminObject == null ? new GmsAdmin() : (GmsAdmin) adminObject;
        if(!adminSessionContinuation.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            responsePayload.setResponseCode(adminSessionContinuation.getResponseCode());
            responsePayload.setResponseMessage(adminSessionContinuation.getResponseMessage());
            adminAuditService.saveAudit(responsePayload, responsePayload.getResponseCode(), operationMessage, admin);
            return responsePayload;
        }

        if(requestPayload.getEndDate() != null && !requestPayload.getEndDate().isEmpty() && !requestPayload.getEndDate().isBlank()){
            if(LocalDate.parse(requestPayload.getStartDate()).isAfter(LocalDate.parse(requestPayload.getEndDate()))){
                responsePayload.setResponseCode(ResponseCode.BAD_MODEL);
                responsePayload.setResponseMessage("End Date cannot be before start date");
                return responsePayload;
            }
        }

        LocalDateTime startDateTime = LocalDate.parse(requestPayload.getStartDate()).atStartOfDay();
        LocalDateTime endDateTime;
        if(requestPayload.getEndDate() == null || requestPayload.getEndDate().isBlank() || requestPayload.getEndDate().isEmpty()){
            endDateTime = startDateTime.plusMonths(1);
        }else{
            endDateTime = LocalDate.parse(requestPayload.getEndDate()).atStartOfDay();
        }

        List<UserDTO> users = userRepository.findAllUsersBetweenDateTimes(startDateTime, endDateTime)
                .stream()
                .map(dto -> mapSimilarObject(dto, UserDTO.class))
                .collect(Collectors.toList());
        ReportResponseData<UserDTO> responseData = new ReportResponseData<>();
        responseData.setData(users);
        responseData.setStartDateTime(startDateTime.toString());
        responseData.setEndDateTime(endDateTime.toString());
        responseData.setRecordCount(users.size());

        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setResponseMessage(messageProvider.getMessage(ResponseCode.SUCCESS));
        responsePayload.setResponseData(responseData);
        return responsePayload;
    }


    @Override
    public TransactionEntriesResponsePayload getTransactionEntries(ReportRequestPayload requestPayload){
        TransactionEntriesResponsePayload responsePayload = new TransactionEntriesResponsePayload();
        String operationMessage = "Fetch transaction entries";

        // Validate that the request is coming from the continuation session of the admin.
        BaseResponse adminSessionContinuation = adminUserService.processAdminUserSessionContinuation(requestPayload.getAdminUser(), requestPayload.getAdminPasscode());
        Object adminObject = adminSessionContinuation.getOtherDetails();
        GmsAdmin admin = adminObject == null ? new GmsAdmin() : (GmsAdmin) adminObject;
        if(!adminSessionContinuation.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            responsePayload.setResponseCode(adminSessionContinuation.getResponseCode());
            responsePayload.setResponseMessage(adminSessionContinuation.getResponseMessage());
            adminAuditService.saveAudit(responsePayload, responsePayload.getResponseCode(), operationMessage, admin);
            return responsePayload;
        }

        if(requestPayload.getEndDate() != null && !requestPayload.getEndDate().isEmpty() && !requestPayload.getEndDate().isBlank()){
            if(LocalDate.parse(requestPayload.getStartDate()).isAfter(LocalDate.parse(requestPayload.getEndDate()))){
                responsePayload.setResponseCode(ResponseCode.BAD_MODEL);
                responsePayload.setResponseMessage("End Date cannot be before start date");
                return responsePayload;
            }
        }

        LocalDateTime startDateTime = LocalDate.parse(requestPayload.getStartDate()).atStartOfDay();
        LocalDateTime endDateTime;
        if(requestPayload.getEndDate() == null || requestPayload.getEndDate().isBlank() || requestPayload.getEndDate().isEmpty()){
            endDateTime = startDateTime.plusMonths(1);
        }else{
            endDateTime = LocalDate.parse(requestPayload.getEndDate()).atStartOfDay();
        }

        List<TransactionEntryDTO> entries = transactionEntryRepository.findAllTransactionEntriesBetweenDate(startDateTime, endDateTime)
                .stream()
                .map(dto -> mapSimilarObject(dto, TransactionEntryDTO.class))
                .collect(Collectors.toList());

        ReportResponseData<TransactionEntryDTO> responseData = new ReportResponseData<>();
        responseData.setData(entries);
        responseData.setStartDateTime(startDateTime.toString());
        responseData.setEndDateTime(endDateTime.toString());
        responseData.setRecordCount(entries.size());

        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setResponseMessage(messageProvider.getMessage(ResponseCode.SUCCESS));
        responsePayload.setResponseData(responseData);
        return responsePayload;
    }


    @Override
    public AlquimiaRemittanceResponsePayload getAlquimiaRemittance(ReportRequestPayload requestPayload){
        AlquimiaRemittanceResponsePayload responsePayload = new AlquimiaRemittanceResponsePayload();
        String operationMessage = "Fetch alquimia remittance";

        // Validate that the request is coming from the continuation session of the admin.
        BaseResponse adminSessionContinuation = adminUserService.processAdminUserSessionContinuation(requestPayload.getAdminUser(), requestPayload.getAdminPasscode());
        Object adminObject = adminSessionContinuation.getOtherDetails();
        GmsAdmin admin = adminObject == null ? new GmsAdmin() : (GmsAdmin) adminObject;
        if(!adminSessionContinuation.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            responsePayload.setResponseCode(adminSessionContinuation.getResponseCode());
            responsePayload.setResponseMessage(adminSessionContinuation.getResponseMessage());
            adminAuditService.saveAudit(responsePayload, responsePayload.getResponseCode(), operationMessage, admin);
            return responsePayload;
        }

        if(requestPayload.getEndDate() != null && !requestPayload.getEndDate().isEmpty() && !requestPayload.getEndDate().isBlank()){
            if(LocalDate.parse(requestPayload.getStartDate()).isAfter(LocalDate.parse(requestPayload.getEndDate()))){
                responsePayload.setResponseCode(ResponseCode.BAD_MODEL);
                responsePayload.setResponseMessage("End Date cannot be before start date");
                return responsePayload;
            }
        }

        LocalDateTime startDateTime = LocalDate.parse(requestPayload.getStartDate()).atStartOfDay();
        LocalDateTime endDateTime;
        if(requestPayload.getEndDate() == null || requestPayload.getEndDate().isBlank() || requestPayload.getEndDate().isEmpty()){
            endDateTime = startDateTime.plusMonths(1);
        }else{
            endDateTime = LocalDate.parse(requestPayload.getEndDate()).atStartOfDay();
        }

        List<AlquimiaRemittanceDTO> remittances = remittanceRepository.findAllRemittanceBetweenDate(startDateTime, endDateTime)
                .stream()
                .map(dto -> mapSimilarObject(dto, AlquimiaRemittanceDTO.class))
                .collect(Collectors.toList());

        ReportResponseData<AlquimiaRemittanceDTO> responseData = new ReportResponseData<>();
        responseData.setData(remittances);
        responseData.setStartDateTime(startDateTime.toString());
        responseData.setEndDateTime(endDateTime.toString());
        responseData.setRecordCount(remittances.size());

        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setResponseMessage(messageProvider.getMessage(ResponseCode.SUCCESS));
        responsePayload.setResponseData(responseData);
        return responsePayload;
    }


    @Override
    public DepositsResponsePayload getDeposits(ReportRequestPayload requestPayload){
        DepositsResponsePayload responsePayload = new DepositsResponsePayload();
        String operationMessage = "Fetch Wire deposits";

        // Validate that the request is coming from the continuation session of the admin.
        BaseResponse adminSessionContinuation = adminUserService.processAdminUserSessionContinuation(requestPayload.getAdminUser(), requestPayload.getAdminPasscode());
        Object adminObject = adminSessionContinuation.getOtherDetails();
        GmsAdmin admin = adminObject == null ? new GmsAdmin() : (GmsAdmin) adminObject;
        if(!adminSessionContinuation.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            responsePayload.setResponseCode(adminSessionContinuation.getResponseCode());
            responsePayload.setResponseMessage(adminSessionContinuation.getResponseMessage());
            adminAuditService.saveAudit(responsePayload, responsePayload.getResponseCode(), operationMessage, admin);
            return responsePayload;
        }

        if(requestPayload.getEndDate() != null && !requestPayload.getEndDate().isEmpty() && !requestPayload.getEndDate().isBlank()){
            if(LocalDate.parse(requestPayload.getStartDate()).isAfter(LocalDate.parse(requestPayload.getEndDate()))){
                responsePayload.setResponseCode(ResponseCode.BAD_MODEL);
                responsePayload.setResponseMessage("End Date cannot be before start date");
                return responsePayload;
            }
        }

        LocalDateTime startDateTime = LocalDate.parse(requestPayload.getStartDate()).atStartOfDay();
        LocalDateTime endDateTime;
        if(requestPayload.getEndDate() == null || requestPayload.getEndDate().isBlank() || requestPayload.getEndDate().isEmpty()){
            endDateTime = startDateTime.plusMonths(1);
        }else{
            endDateTime = LocalDate.parse(requestPayload.getEndDate()).atStartOfDay();
        }

        List<AccountDepositDTO> deposits = accountDepositRepository.findAllAccountDepositBetweenDate(startDateTime, endDateTime)
                .stream()
                .map(dto -> mapSimilarObject(dto, AccountDepositDTO.class))
                .collect(Collectors.toList());

        ReportResponseData<AccountDepositDTO> responseData = new ReportResponseData<>();
        responseData.setData(deposits);
        responseData.setStartDateTime(startDateTime.toString());
        responseData.setEndDateTime(endDateTime.toString());
        responseData.setRecordCount(deposits.size());

        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setResponseMessage(messageProvider.getMessage(ResponseCode.SUCCESS));
        responsePayload.setResponseData(responseData);
        return responsePayload;
    }

    private <T> T mapSimilarObject(Object from, Class<T> toType){
        String json = JSON.toJson(from);
        return JSON.fromJson(json, toType);
    }
}
