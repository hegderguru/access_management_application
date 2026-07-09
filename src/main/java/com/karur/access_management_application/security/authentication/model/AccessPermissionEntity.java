package com.karur.access_management_application.security.authentication.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "accessor_authority_role_permission",schema = "creds")
public class AccessPermissionEntity {

    @Id
    private Long id;

    private String roleName;
    private String classPath;
    private String fieldName;
    private boolean read;
    private boolean create;
    private boolean update;
    private boolean delete;

    private Long roleId;
}
