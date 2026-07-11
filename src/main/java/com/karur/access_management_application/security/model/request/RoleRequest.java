package com.karur.access_management_application.security.model.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {
    private String name;
    private String description;

    private List<PermissionRequest> permissionRequests;
}
