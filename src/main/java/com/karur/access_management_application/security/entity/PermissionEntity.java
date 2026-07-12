package com.karur.access_management_application.security.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "permission",schema = "auth")
public class PermissionEntity {

    @Id
    private Long id;
    private String classPath;
    private String className;
    private String fieldName;
    private boolean read;
    private boolean create;
    private boolean update;
    private boolean delete;

    public String fullyQualifiedClassPath() {
        return classPath + "." + className;
    }

    public String fullyQualifiedFieldPath() {
        return fullyQualifiedClassPath() + "." + fieldName;
    }

    public Boolean[] permissions() {
        return new Boolean[]{read, create, update, delete};
    }

}
