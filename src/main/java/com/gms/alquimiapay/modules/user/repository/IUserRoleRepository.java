package com.gms.alquimiapay.modules.user.repository;

import com.gms.alquimiapay.constants.RoleName;
import com.gms.alquimiapay.modules.user.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IUserRoleRepository extends JpaRepository<UserRole, Long>
{
    UserRole findByRoleName(String roleName);

    default UserRole getDefaultOrdinaryUserRole(){
        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setRoleName(RoleName.USER.name());
        userRole.setCreatedAt(LocalDateTime.now().toString());
        userRole.setUpdatedAt(userRole.getCreatedAt());
        return this.saveAndFlush(userRole);
    }
}
