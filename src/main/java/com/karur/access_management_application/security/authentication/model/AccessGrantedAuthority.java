package com.karur.access_management_application.security.authentication.model;

import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class AccessGrantedAuthority implements GrantedAuthority {

    private String name;
    private List<AccessRole> authorityRoles = new ArrayList<>();

    @Override
    public @Nullable String getAuthority() {
        return name;
    }

    @Data
    private static class AccessRole {
        private String name;
        private AccessPermission accessPermission;

        private static class AccessPermission {
            private String classPath;
            private String fieldName;
            private boolean read;
            private boolean create;
            private boolean update;
            private boolean delete;
        }
    }
}
