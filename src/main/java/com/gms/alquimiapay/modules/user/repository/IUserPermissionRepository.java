package com.gms.alquimiapay.modules.user.repository;

import com.gms.alquimiapay.modules.user.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IUserPermissionRepository extends JpaRepository<UserPermission, Long>
{
    List<UserPermission> findByRoleId(Long roleId);
}
