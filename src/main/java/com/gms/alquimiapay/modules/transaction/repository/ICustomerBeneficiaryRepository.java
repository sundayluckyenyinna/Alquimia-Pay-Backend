package com.gms.alquimiapay.modules.transaction.repository;

import com.gms.alquimiapay.modules.transaction.model.CustomerBeneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICustomerBeneficiaryRepository extends JpaRepository<CustomerBeneficiary, Long> {

    List<CustomerBeneficiary> findByOwnerCustomerEmail(String customerEmail);
}
