package com.gms.alquimiapay.modules.transaction.repository;

import com.gms.alquimiapay.modules.transaction.model.BlockchainAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBlockchainAddressRepository extends JpaRepository<BlockchainAddress, Long>
{
    BlockchainAddress findByAddress(String address);
}
