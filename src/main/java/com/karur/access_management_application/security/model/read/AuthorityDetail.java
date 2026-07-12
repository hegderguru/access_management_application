package com.karur.access_management_application.security.model.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDetail implements GrantedAuthority {
    private String name;
    private String description;
    private List<RoleDetail> roleDetails;

    @Override
    public @Nullable String getAuthority() {
        return name;
    }
}