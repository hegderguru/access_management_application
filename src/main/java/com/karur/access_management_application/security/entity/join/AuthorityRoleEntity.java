package com.karur.access_management_application.security.entity.join;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(value = "authority_role_id", schema = "auth")
public class AuthorityRoleEntity {
    @Id
    private Long id;
    private Long authorityId;
    private Long roleId;
}
