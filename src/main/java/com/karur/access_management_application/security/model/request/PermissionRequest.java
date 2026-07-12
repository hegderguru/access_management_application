package com.karur.access_management_application.security.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.karur.access_management_application.security.compare.DiffId;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldNameConstants(asEnum = true)
public class PermissionRequest {

    private String classPath;
    private String className;
    private String fieldName;
    private Boolean read;
    private Boolean create;
    private Boolean update;
    private Boolean delete;
    private String fullyQualifiedClassPath;
    @DiffId
    private String fullyQualifiedFieldPath;
    private Boolean[] permissions;

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

    public Boolean[] permissions() {
        return new Boolean[]{read, create, update, delete};
    }
}
