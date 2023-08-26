package com.gms.alquimiapay.dao;

import com.gms.alquimiapay.constants.Creator;
import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.model.GmsParam;
import com.gms.alquimiapay.model.MasterBankAccount;
import com.gms.alquimiapay.model.MasterWallet;
import com.gms.alquimiapay.modules.account.payload.data.AccountBillingDetails;
import com.gms.alquimiapay.modules.account.payload.data.BankAddress;
import com.gms.alquimiapay.modules.kyc.constant.Vendor;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;


@Repository
@Transactional
public class GmsDAO implements IGmsDAO {

    @Autowired
    private Environment env;

    @PersistenceContext
    private EntityManager em;

    private static final Gson JSON = new Gson();

    @Override
    public GmsParam getParamByKey(String key) {
        return em.createQuery("Select t from GmsParam t where t.paramKey = :key", GmsParam.class)
                .setParameter("key", key)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public MasterWallet getMasterWallet(){
     return em.createQuery("select w from MasterWallet w", MasterWallet.class)
             .getResultList()
             .stream()
             .findFirst()
             .orElse(null);
    }

    @Override
    public MasterBankAccount getMasterBankAccount(){
        return em.createQuery("select a from MasterBankAccount a where a.status = :status", MasterBankAccount.class)
                .setParameter("status", ModelStatus.ACTIVE.name())
                .getResultList()
                .stream()
                .findFirst()
                .orElse(getDefaultMasterAccount());
    }

    @Override
    public GmsParam saveParam(GmsParam gmsParam) {
        em.persist(gmsParam);
        return gmsParam;
    }

    @Override
    public GmsParam updateParam(GmsParam gmsParam) {
        em.merge(gmsParam);
        return gmsParam;
    }

    private MasterBankAccount getDefaultMasterAccount(){
        MasterBankAccount result  = new MasterBankAccount();
        result.setAccountNumber(env.getProperty("third-party.circle.master-account.base.account-number"));
        result.setStatus(ModelStatus.ACTIVE.name());
        result.setInternalRef(UUID.randomUUID().toString());
        result.setRoutingNumber(env.getProperty("third-party.circle.master-account.base.routing-number"));

        AccountBillingDetails billingDetails = new AccountBillingDetails();
        billingDetails.setCity(env.getProperty("third-party.circle.master-account.base.billing-details.city"));
        billingDetails.setDistrict(env.getProperty("third-party.circle.master-account.base.billing-details.district"));
        billingDetails.setCountry(env.getProperty("third-party.circle.master-account.base.billing-details.country"));
        billingDetails.setLine1(env.getProperty("third-party.circle.master-account.base.billing-details.line1"));
        billingDetails.setLine2(env.getProperty("third-party.circle.master-account.base.billing-details.line2"));
        billingDetails.setPostalCode(env.getProperty("third-party.circle.master-account.base.billing-details.postal-code"));

        BankAddress bankAddress = new BankAddress();
        bankAddress.setBankName(env.getProperty("third-party.circle.master-account.base.bank-address.bank-name"));
        bankAddress.setCity(env.getProperty("third-party.circle.master-account.base.bank-address.city"));
        bankAddress.setCountry(env.getProperty("third-party.circle.master-account.base.bank-address.country"));
        bankAddress.setDistrict(env.getProperty("third-party.circle.master-account.base.bank-address.district"));
        bankAddress.setLine1(env.getProperty("third-party.circle.master-account.base.bank-address.line1"));
        bankAddress.setLine2(env.getProperty("third-party.circle.master-account.base.bank-address.line2"));

        result.setBankAddressJson(JSON.toJson(bankAddress));
        result.setBillingDetailsJson(JSON.toJson(billingDetails));

        em.persist(result);
        em.flush();

        return result;
    }

    @Override
    public String getActiveVendor(){
        GmsParam param = this.getParamByKey("ACTIVE_VENDOR");
        if(param == null){
            param = new GmsParam();
            param.setParamKey("ACTIVE_VENDOR");
            param.setParamValue(Vendor.CIRCLE.name());
            param.setCreatedAt(LocalDateTime.now().toString());
            param.setCreatedBy(Creator.SYSTEM.name());
            param.setParamDesc("Third Party Vendor");
            param.setUpdatedBy(Creator.SYSTEM.name());

            GmsParam savedParam = this.saveParam(param);
            return savedParam.getParamValue();
        }
        return param.getParamValue();
    }
}
