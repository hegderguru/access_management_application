package com.karur.access_management_application.security.model.request;

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
}
