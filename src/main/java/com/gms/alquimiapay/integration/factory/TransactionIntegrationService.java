package com.gms.alquimiapay.integration.factory;

import com.gms.alquimiapay.dao.GmsDAO;
import com.gms.alquimiapay.integration.external.circle.service.transaction.CircleTransactionService;
import com.gms.alquimiapay.integration.external.xe.service.XeExchangeRateService;
import com.gms.alquimiapay.integration.internal.transaction.ITransactionIntegrationService;
import com.gms.alquimiapay.modules.kyc.constant.Vendor;
import com.gms.alquimiapay.modules.transaction.payload.pojo.TransactionPojo;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionIntegrationService implements ITransactionIntegrationService
{

    private final GmsDAO gmsDAO;

    private final CircleTransactionService circleTransactionService;
    private final XeExchangeRateService xeExchangeRateService;

    @Override
    public BaseResponse processTransactionFeeRequest(String amount, String transactionType) {
        String activeVendor = gmsDAO.getActiveVendor();

        log.info("Active Vendor found: {}", activeVendor);
        if(activeVendor.equalsIgnoreCase(Vendor.CIRCLE.name())){
            return circleTransactionService.processTransactionFeeRequest(amount, transactionType);
        }

        return null;
    }

    @Override
    public BaseResponse processCashToBlockchainTransactionRequest(TransactionPojo pojo) {

        String activeVendor = gmsDAO.getActiveVendor();

        log.info("Active Vendor found: {}", activeVendor);
        if(activeVendor.equalsIgnoreCase(Vendor.CIRCLE.toString())){
            return circleTransactionService.processCashToBlockchainTransactionRequest(pojo);
        }

        return null;
    }


    @Override
    public BaseResponse processCashToBlockchainTransactionStatus(String id){
        String activeVendor = gmsDAO.getActiveVendor();

        log.info("Active Vendor found: {}", activeVendor);
        if(activeVendor.equalsIgnoreCase(Vendor.CIRCLE.toString())){
            return circleTransactionService.processCashToBlockchainTransactionStatus(id);
        }

        return null;
    }

    @Override
    public BaseResponse processExchangeRate(String fromCurrency, String toCurrency) {
        String defaultUsdAmountForExchangeRate = "1.00";
        return xeExchangeRateService.getExchangeRate(defaultUsdAmountForExchangeRate, fromCurrency, toCurrency);
    }
}
