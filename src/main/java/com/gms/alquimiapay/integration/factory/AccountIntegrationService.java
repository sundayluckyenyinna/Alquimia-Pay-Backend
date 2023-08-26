package com.gms.alquimiapay.integration.factory;

import com.gms.alquimiapay.constants.QualifierValue;
import com.gms.alquimiapay.integration.internal.account.IAccountIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AccountIntegrationService
{

    @Autowired
    @Qualifier(QualifierValue.CIRCLE_PARTY_ACCOUNT_SERVICE)
    private IAccountIntegrationService circleAccountIntegrationService;

}
