package com.karur.access_management_application.security.util;

import com.karur.access_management_application.security.model.request.AccessRequest;

import java.util.ArrayList;
import java.util.Objects;

public class AccessRequestUtil {

    public static AccessRequest buildAccessRequest(AccessRequest accessRequest) {
        if (Objects.isNull(accessRequest.getAuthorityRequests())) {
            accessRequest.setAuthorityRequests(new ArrayList<>());
        }
        accessRequest.getAuthorityRequests().forEach(authorityRequest -> {
            if (Objects.isNull(authorityRequest.getRoleRequests())) {
                authorityRequest.setRoleRequests(new ArrayList<>());
            }
            authorityRequest.getRoleRequests().forEach(roleRequest -> {
                if (Objects.isNull(roleRequest.getPermissionRequests())) {
                    roleRequest.setPermissionRequests(new ArrayList<>());
                }
            });
        });
        return accessRequest;
    }
}
