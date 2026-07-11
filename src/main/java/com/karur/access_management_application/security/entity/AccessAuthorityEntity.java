package com.karur.access_management_application.security.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "access_authority_id",schema = "auth")
public record AccessAuthorityEntity(@Id Long id, Long accessId, Long authorityId) {
}
