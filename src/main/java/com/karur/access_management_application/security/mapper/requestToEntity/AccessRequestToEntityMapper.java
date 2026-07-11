package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.model.request.AccessorRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import org.springframework.stereotype.Service;

@Service
public class AccessRequestToEntityMapper {

    public AccessEntity buildAccessorEntity(AccessorRequest accessorRequest) {
        return AccessEntity.builder()
                .username(accessorRequest.getUsername())
                .password(accessorRequest.getPassword())
                .firstName(accessorRequest.getFirstName())
                .middleName(accessorRequest.getMiddleName())
                .lastName(accessorRequest.getLastName())
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
