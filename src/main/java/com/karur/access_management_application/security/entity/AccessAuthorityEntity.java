package com.karur.access_management_application.security.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Builder
@Table(value = "access_authority_id",schema = "auth")
public record AccessAuthorityEntity(@Id Long id, Long accessId, Long authorityId) {
}
