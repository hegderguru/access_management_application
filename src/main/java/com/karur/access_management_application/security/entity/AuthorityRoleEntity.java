package com.karur.access_management_application.security.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table(value = "authority_role_id",schema = "auth")
public record AuthorityRoleEntity(@Id Long id, Long authorityId, Long roleId) {
}
