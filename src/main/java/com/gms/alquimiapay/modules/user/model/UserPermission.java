package com.gms.alquimiapay.modules.user.model;

import javax.persistence.*;

@Entity
@Table(name = "gms_user_permission")
public class UserPermission
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "permission_name")
    private String permissionName;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

}
