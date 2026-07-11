package com.karur.access_management_application.security.authentication.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "access_authority_role_permission",schema = "creds")
public class PermissionEntity {

    @Id
    private Long id;

    private String roleName;
    private String classPath;
    private String className;
    private String fieldName;
    private boolean read;
    private boolean create;
    private boolean update;
    private boolean delete;

    private Long roleId;
}
