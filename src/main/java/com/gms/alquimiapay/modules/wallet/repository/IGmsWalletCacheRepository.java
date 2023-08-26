package com.gms.alquimiapay.modules.wallet.repository;

import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGmsWalletCacheRepository extends JpaRepository<GmsWalletCache, Long>
{
    GmsWalletCache findByWalletId(String walletId);
    GmsWalletCache findByOwnerEmail(String email);
}
