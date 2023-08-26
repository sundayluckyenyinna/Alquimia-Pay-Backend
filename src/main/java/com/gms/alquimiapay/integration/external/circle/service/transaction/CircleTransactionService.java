package com.gms.alquimiapay.integration.external.circle.service.transaction;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.*;
import com.gms.alquimiapay.dao.GmsDAO;
import com.gms.alquimiapay.integration.external.circle.constant.CircleApiPath;
import com.gms.alquimiapay.integration.external.circle.dto.CircleErrorResponse;
import com.gms.alquimiapay.integration.external.circle.dto.deposit.data.CircleAmount;
import com.gms.alquimiapay.integration.external.circle.dto.transaction.data.BlockchainAddressDTO;
import com.gms.alquimiapay.integration.external.circle.dto.transaction.data.BlockchainAddressListDTO;
import com.gms.alquimiapay.integration.external.circle.dto.transaction.data.TransactionAddress;
import com.gms.alquimiapay.integration.external.circle.dto.transaction.request.TransferRequestDTO;
import com.gms.alquimiapay.integration.external.circle.dto.transaction.response.TransferResponseDTO;
import com.gms.alquimiapay.integration.external.circle.service.ICircleGenericService;
import com.gms.alquimiapay.model.GmsParam;
import com.gms.alquimiapay.modules.kyc.constant.Vendor;
import com.gms.alquimiapay.modules.transaction.model.BlockchainAddress;
import com.gms.alquimiapay.modules.transaction.payload.pojo.TransactionPojo;
import com.gms.alquimiapay.modules.transaction.repository.IBlockchainAddressRepository;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service(value = QualifierValue.CIRCLE_PARTY_TRANSACTION_SERVICE)
public class CircleTransactionService
{
    @Autowired
    private ICircleGenericService genericService;
    @Autowired
    private MessageProvider messageProvider;
    @Autowired
    private IBlockchainAddressRepository blockchainAddressRepository;
    @Autowired
    private GmsDAO gmsDAO;

    private static final Gson JSON = new Gson();

    private static final String CIRCLE_PARAMS_KEY_PREFIX = Vendor.CIRCLE.name().concat(StringValues.UNDER_SCORE);

    public BaseResponse processTransactionFeeRequest(String amount, String transactionType) {
        BaseResponse response = new BaseResponse();
        String code;

        String key = CIRCLE_PARAMS_KEY_PREFIX.concat(transactionType).concat(StringValues.UNDER_SCORE).concat("FEE");
        GmsParam param = gmsDAO.getParamByKey(key);
        if(param == null)
            param = this.getDefaultCircleTransactionFee(key);

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        response.setOtherDetails(param.getParamValue());
        return response;
    }

    public BaseResponse processCashToBlockchainTransactionRequest(TransactionPojo pojo) {
        BaseResponse response = new BaseResponse();
        String code;

        String url = genericService.resolveCircleApiPath(CircleApiPath.ACCOUNT_TRANSFER);

        String address = pojo.getDestinationBlockchainAddress().trim();
        String addressTag = pojo.getDestinationBlockchainAddressTag();
        String description = "Blockchain recipient address for Alquimia";
        String ccy = pojo.getCurrency();
        String chain = pojo.getChain();

        BlockchainAddress blockchainAddress = blockchainAddressRepository.findAll().stream().filter(f -> f.getAddress().equalsIgnoreCase(address)).findFirst().orElse(null);
        if(blockchainAddress == null){
            BaseResponse baseResponse = this.processBlockChainAddress(address, chain, addressTag, description, ccy);
            BlockchainAddressDTO data = (BlockchainAddressDTO) baseResponse.getOtherDetails();

            blockchainAddress = new BlockchainAddress();
            blockchainAddress.setAddressTag(data.getAddressTag());
            blockchainAddress.setDescription(data.getDescription());
            blockchainAddress.setChain(data.getChain());
            blockchainAddress.setExternalRef(data.getId());
            blockchainAddress.setAddress(data.getAddress());
            blockchainAddress.setStatus(ModelStatus.COMPLETE.name());
            blockchainAddress.setInternalRef(UUID.randomUUID().toString());
            blockchainAddress.setCreatedAt(LocalDateTime.now().toString());
            blockchainAddress.setUpdatedAt(LocalDateTime.now().toString());
            blockchainAddress.setOwnerCustomerName(pojo.getCustomerName());
            blockchainAddress.setOwnerCustomerEmail(pojo.getCustomerEmail());
            blockchainAddress.setCurrency(ccy);

            blockchainAddressRepository.saveAndFlush(blockchainAddress);
        }

        String addressId = blockchainAddress.getExternalRef();

        TransactionAddress destination = new TransactionAddress();
        destination.setType("verified_blockchain");
        destination.setAddressId(addressId);

        CircleAmount amount = new CircleAmount();
        amount.setAmount(pojo.getAmount());
        amount.setCurrency(ccy);

        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setIdempotencyKey(UUID.randomUUID().toString());
        requestDTO.setDestination(destination);
        requestDTO.setAmount(amount);

        log.info("RequestJson to create account transfer: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = genericService.postExchangeWithCircle(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));

            log.info("Circle service unavailable");
            return response;
        }

        CircleErrorResponse errorResponse = JSON.fromJson(webResponse.getSuccessResponseJson(), CircleErrorResponse.class);
        if(errorResponse.getCode() != null){
            code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            response.setResponseCode(code);
            response.setResponseMessage(errorResponse.getMessage());

            log.info("Circle third-party failure Json: {}", webResponse.getSuccessResponseJson());
            return response;
        }

        log.info("ResponseJson to create account transfer: {}", webResponse.getSuccessResponseJson());

        String dataJson = genericService.getCircleDataJson(webResponse.getSuccessResponseJson());
        TransferResponseDTO responseDTO = JSON.fromJson(dataJson, TransferResponseDTO.class);

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        response.setOtherDetails(responseDTO);

        return response;
    }


    public BaseResponse processCashToBlockchainTransactionStatus(String id){
        BaseResponse response = new BaseResponse();
        String code;

        String url = genericService.resolveCircleApiPath(CircleApiPath.ACCOUNT_TRANSFER_STATUS);
        url = url.replace("{id}", id);

        log.info("GET URL to fetch transaction status: {}", url);

        WebResponse webResponse = genericService.getExchangeWithCircle(url);
        if(webResponse.isHasConnectionError()){
            code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));

            log.info("Circle service unavailable");
            return response;
        }

        CircleErrorResponse errorResponse = JSON.fromJson(webResponse.getSuccessResponseJson(), CircleErrorResponse.class);
        if(errorResponse.getCode() != null){
            code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            response.setResponseCode(code);
            response.setResponseMessage(errorResponse.getMessage());

            log.info("Circle third-party failure Json: {}", webResponse.getSuccessResponseJson());
            return response;
        }

        log.info("ResponseJson to fetch transaction status: {}", webResponse.getSuccessResponseJson());
        String dataJson = genericService.getCircleDataJson(webResponse.getSuccessResponseJson());

        TransferResponseDTO responseDTO = JSON.fromJson(dataJson, TransferResponseDTO.class);
        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        response.setOtherDetails(responseDTO);

        return response;
    }

    public BaseResponse processBlockChainAddress(String address, String chain, String addressTag, String description, String ccy){
        BaseResponse response = new BaseResponse();
        String code;

        // First check if the address is saved remotely
        List<BlockchainAddressDTO> addressDTOS = this.getAllRemoteRecipientAddresses();
        BlockchainAddressDTO addressDTO = addressDTOS.stream().filter(a -> a.getAddress().equalsIgnoreCase(address)).findFirst().orElse(null);
        if(addressDTO != null){
            code = ResponseCode.SUCCESS;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            response.setOtherDetails(addressDTO);
            return response;
        }

        // Create and return a new address.
        String url = genericService.resolveCircleApiPath(CircleApiPath.BLOCKCHAIN_RECIPIENT_ADDRESS);
        BlockchainAddressDTO requestDTO = new BlockchainAddressDTO();
        requestDTO.setAddress(address);
        requestDTO.setChain(chain);
        requestDTO.setAddressTag(addressTag);
        requestDTO.setCurrency(ccy);
        requestDTO.setIdempotencyKey(UUID.randomUUID().toString());
        requestDTO.setDescription(description);

        log.info("RequestJson to create Blockchain address: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = genericService.postExchangeWithCircle(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));

            log.info("Circle service unavailable");
            return response;
        }

        CircleErrorResponse errorResponse = JSON.fromJson(webResponse.getSuccessResponseJson(), CircleErrorResponse.class);
        if(errorResponse.getCode() != null){
            code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            response.setResponseCode(code);
            response.setResponseMessage(errorResponse.getMessage());

            log.info("Circle third-party failure Json: {}", webResponse.getSuccessResponseJson());
            return response;
        }

        log.info("ResponseJson to create Blockchain address: {}", webResponse.getSuccessResponseJson());

        String dataJson = genericService.getCircleDataJson(webResponse.getSuccessResponseJson());
        BlockchainAddressDTO responseDTO = JSON.fromJson(dataJson, BlockchainAddressDTO.class);

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        response.setOtherDetails(responseDTO);
        return response;
    }

    public List<BlockchainAddressDTO> getAllRemoteRecipientAddresses(){
        String url = genericService.resolveCircleApiPath(CircleApiPath.BLOCKCHAIN_RECIPIENT_ADDRESS);
        WebResponse webResponse = genericService.getExchangeWithCircle(url);
        String responseJson = webResponse.getSuccessResponseJson();
        BlockchainAddressListDTO addressListDTO = JSON.fromJson(responseJson, BlockchainAddressListDTO.class);
        return addressListDTO.getData();
    }


    private GmsParam getDefaultCircleTransactionFee(String key){
        GmsParam param = new GmsParam();
        param.setParamKey(key);
        param.setParamValue("0.00");
        param.setParamDesc("Transaction fee for Circle integration");
        param.setCreatedAt(LocalDateTime.now().toString());
        param.setCreatedBy(Creator.SYSTEM.name());
        param.setUpdatedAt(LocalDateTime.now().toString());
        param.setUpdatedBy(Creator.SYSTEM.name());
        return gmsDAO.saveParam(param);
    }
}
