package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import org.springframework.stereotype.Service;

@Service
public class AccessRequestToEntityMapper {

    public AccessEntity buildAccessEntity(AccessRequest accessRequest) {
        return AccessEntity.builder()
                .username(accessRequest.getUsername())
                .password(accessRequest.getPassword())
                .firstName(accessRequest.getFirstName())
                .middleName(accessRequest.getMiddleName())
                .lastName(accessRequest.getLastName())
                .accessEnabled(true)
                .accessExpired(false)
                .credentialsExpired(false)
                .accessLocked(false)
                .build();
    }

    public RoleEntity buildRoleEntity(RoleRequest roleRequest) {
        return RoleEntity.builder()
                .name(roleRequest.getName())
                .description(roleRequest.getDescription())
                .build();
    }

    public PermissionEntity buildPermissionEntity(PermissionRequest permissionRequest) {
        return PermissionEntity.builder()
                .classPath(permissionRequest.getClassPath())
                .className(permissionRequest.getClassName())
                .fieldName(permissionRequest.getFieldName())
                .read(permissionRequest.isRead())
                .create(permissionRequest.isCreate())
                .update(permissionRequest.isUpdate())
                .delete(permissionRequest.isDelete())
                .build();
    }

}
