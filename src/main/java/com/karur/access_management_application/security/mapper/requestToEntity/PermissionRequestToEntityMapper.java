package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.entity.RolePermissionEntity;
import com.karur.access_management_application.security.model.request.PermissionRequest;

import java.util.List;

public class PermissionRequestToEntityMapper {

    public PermissionEntity buildPermissionEntity(PermissionRequest permissionRequest) {
        return PermissionEntity.builder()
                .classPath(permissionRequest.getClassPath())
                .className(permissionRequest.getClassName())
                .fieldName(permissionRequest.getFieldName())
                .read(permissionRequest.getRead())
                .create(permissionRequest.getCreate())
                .update(permissionRequest.getUpdate())
                .delete(permissionRequest.getDelete())
                .build();
    }

    public List<RolePermissionEntity> buildRolePermissionEntities(RoleEntity roleEntity, List<PermissionEntity> permissionEntities) {
        return permissionEntities.stream().map(permissionEntity -> buildRolePermissionEntity(roleEntity.getId(), permissionEntity)).toList();
    }

    public RolePermissionEntity buildRolePermissionEntity(Long roleId, PermissionEntity permissionEntity) {
        return RolePermissionEntity.builder()
                .roleId(roleId)
                .permissionId(permissionEntity.getId())
                .build();
    }
}
