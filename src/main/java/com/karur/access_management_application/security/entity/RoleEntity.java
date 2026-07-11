package com.karur.access_management_application.security.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Table(name = "access_authority_role", schema = "auth")
public class RoleEntity {

    @Id
    private Long id;

    private String name;
    private String description;

    @Transient
    private List<PermissionEntity> accessPermissionEntities = new ArrayList<>();
}
