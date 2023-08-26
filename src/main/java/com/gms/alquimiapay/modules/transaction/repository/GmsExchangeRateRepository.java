package com.gms.alquimiapay.modules.transaction.repository;

import com.gms.alquimiapay.modules.transaction.model.GmsExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GmsExchangeRateRepository extends JpaRepository<GmsExchangeRate, Long>
{
    GmsExchangeRate findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
}
