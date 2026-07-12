package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.*;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
                .authorityEntities(new ArrayList<>())
                .build();
    }

    public AuthorityEntity buildAuthorityEntity(AuthorityRequest authorityRequest) {
        return AuthorityEntity.builder()
                .roleEntities(new ArrayList<>())
                .name(authorityRequest.getName())
                .description(authorityRequest.getDescription())
                .build();
    }

    public List<AccessAuthorityEntity> buildAccessAuthorityEntities(AccessEntity accessEntity, List<AuthorityEntity> authorityEntities) {
        return authorityEntities.stream().map(authorityEntity -> buildAccessAuthorityEntity(accessEntity.getId(), authorityEntity)).toList();
    }

    public AccessAuthorityEntity buildAccessAuthorityEntity(Long accessId, AuthorityEntity authorityEntity) {
        return AccessAuthorityEntity.builder()
                .accessId(accessId)
                .authorityId(authorityEntity.getId())
                .build();
    }

    public RoleEntity buildRoleEntity(RoleRequest roleRequest) {
        return RoleEntity.builder()
                .name(roleRequest.getName())
                .description(roleRequest.getDescription())
                .build();
    }

    public List<AuthorityRoleEntity> buildAuthorityRoleEntities(AuthorityEntity authorityEntity, List<RoleEntity> roleEntities) {
        return roleEntities.stream().map(roleEntity -> buildAuthorityRoleEntity(authorityEntity.getId(), roleEntity)).toList();
    }

    public AuthorityRoleEntity buildAuthorityRoleEntity(Long authorityId, RoleEntity roleEntity) {
        return AuthorityRoleEntity.builder()
                .authorityId(authorityId)
                .roleId(roleEntity.getId())
                .build();
    }

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
