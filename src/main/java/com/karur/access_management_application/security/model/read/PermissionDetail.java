package com.karur.access_management_application.security.model.read;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionDetail {
    private String classPath;
    private String className;
    private String fieldName;
    private boolean read;
    private boolean create;
    private boolean update;
    private boolean delete;
}
