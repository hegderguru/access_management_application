package com.karur.access_management_application.security.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table(value = "role_permission_id",schema = "auth")
public record RolePermissionEntity(@Id Long id, Long roleId, Long permissionId) {
}
