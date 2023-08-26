package com.gms.alquimiapay.modules.generic.service;

import com.gms.alquimiapay.modules.generic.constant.HashAlgo;
import com.gms.alquimiapay.modules.generic.payload.LookupDataResponsePayload;

import java.io.File;

public interface IGenericService
{
    LookupDataResponsePayload processLookupData(String lookupType);

    String hashFileContent(File file, HashAlgo algo);

    String getBinaryDataFromFile(File file);
}
