package com.karur.access_management_application.security.authentication.model;

import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Table(value = "accessor_authority",schema = "creds")
public class AccessGrantedAuthorityEntity implements GrantedAuthority {

    @Id
    private Long id;

    private String name;

    private Long accessorId;

    @Transient
    private List<AccessRoleEntity> accessRoleEntities = new ArrayList<>();

    @Override
    public @Nullable String getAuthority() {
        return name;
    }

}
