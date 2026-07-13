package com.karur.access_management_application.security.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Builder
@Table(value = "access_authority_id", schema = "auth")
public class AccessAuthorityEntity {
    @Id
    private Long id;
    private Long accessId;
    private Long authorityId;
}
