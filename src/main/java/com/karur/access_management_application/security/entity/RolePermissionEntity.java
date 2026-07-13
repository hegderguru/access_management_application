package com.karur.access_management_application.security.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(value = "role_permission_id", schema = "auth")
public class RolePermissionEntity {
    @Id
    private Long id;
    private Long roleId;
    private Long permissionId;
}
