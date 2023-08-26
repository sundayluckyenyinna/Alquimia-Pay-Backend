package com.gms.alquimiapay.dao;

import com.gms.alquimiapay.model.GmsParam;
import com.gms.alquimiapay.model.MasterBankAccount;
import com.gms.alquimiapay.model.MasterWallet;

public interface IGmsDAO {
    GmsParam getParamByKey(String key);

    MasterWallet getMasterWallet();

    MasterBankAccount getMasterBankAccount();

    GmsParam saveParam(GmsParam gmsParam);
    GmsParam updateParam(GmsParam gmsParam);

    String getActiveVendor();
}
