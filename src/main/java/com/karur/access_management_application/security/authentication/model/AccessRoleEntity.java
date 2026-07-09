package com.karur.access_management_application.security.authentication.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "accessor_authority_role",schema = "creds")
public class AccessRoleEntity {

    @Id
    private Long id;

    private String name;

    private Long authorityId;

    @Transient
    private List<AccessPermissionEntity> accessPermissionEntities = new ArrayList<>();
}
