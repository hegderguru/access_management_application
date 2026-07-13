package com.karur.access_management_application.security.entity;

import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Table(value = "authority",schema = "auth")
public class AuthorityEntity {

    @Id
    private Long id;

    private String name;
    private String description;

    @Transient
    private List<RoleEntity> roleEntities = new ArrayList<>();

}
