package com.karur.access_management_application.security.model.request;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequest {

    private String classPath;
    private String className;
    private String fieldName;
    private boolean read;
    private boolean create;
    private boolean update;
    private boolean delete;
    private String fullyQualifiedClassPath;
    private String fullyQualifiedFieldPath;
    private boolean[] permissions;

    @PostConstruct
    public void init() {
        fullyQualifiedClassPath = fullyQualifiedClassPath();
        fullyQualifiedFieldPath = fullyQualifiedFieldPath();
        permissions = permissions();
    }

    public String fullyQualifiedClassPath() {
        return classPath + "." + className;
    }

    public String fullyQualifiedFieldPath() {
        return fullyQualifiedClassPath() + "." + fieldName;
    }

    public boolean[] permissions() {
        return new boolean[]{read, create, update, delete};
    }
}
