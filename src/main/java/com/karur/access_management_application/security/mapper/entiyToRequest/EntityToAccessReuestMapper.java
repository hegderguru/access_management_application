package com.karur.access_management_application.security.mapper.entiyToRequest;

import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.util.CommonUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntityToAccessReuestMapper {

    public AccessRequest buildAccessRequest(AccessEntity accessEntity) {
        return AccessRequest.builder()
                .username(accessEntity.getUsername())
                .password(accessEntity.getPassword())
                .firstName(accessEntity.getFirstName())
                .middleName(accessEntity.getMiddleName())
                .lastName(accessEntity.getLastName())
                .accessEnabled(accessEntity.isAccessEnabled())
                .accessLocked(accessEntity.isAccessLocked())
                .accessExpired(accessEntity.isAccessExpired())
                .credentialsExpired(accessEntity.isCredentialsExpired())
                .authorityRequests(buildAuthorityRequests(accessEntity.getAuthorityEntities()))
                .build();
    }

    public List<AuthorityRequest> buildAuthorityRequests(List<AuthorityEntity> authorityEntities) {
        return CommonUtil.returnListElseEmpty(authorityEntities).stream().map(this::buildAuthorityRequest).toList();
    }

    public AuthorityRequest buildAuthorityRequest(AuthorityEntity authorityEntity) {
        return AuthorityRequest.builder()
                .name(authorityEntity.getName())
                .description(authorityEntity.getDescription())
                .roleRequests(buildRoleDetails(authorityEntity.getRoleEntities()))
                .build();
    }

    private List<RoleRequest> buildRoleDetails(List<RoleEntity> accessRoleEntities) {
        return CommonUtil.returnListElseEmpty(accessRoleEntities).stream().map(this::buildRoleRequest).toList();
    }

    public RoleRequest buildRoleRequest(RoleEntity roleEntity) {
        return RoleRequest.builder()
                .name(roleEntity.getName())
                .description(roleEntity.getDescription())
                .permissionRequests(buildPermissionRequests(roleEntity.getPermissionEntities()))
                .build();
    }

    private List<PermissionRequest> buildPermissionRequests(List<PermissionEntity> accessPermissionEntities) {
        return CommonUtil.returnListElseEmpty(accessPermissionEntities).stream().map(this::buildPermissionRequest).toList();
    }

    private PermissionRequest buildPermissionRequest(PermissionEntity permissionEntity) {
        return PermissionRequest.builder()
                .fullyQualifiedFieldName(permissionEntity.getFullyQualifiedFieldName())
                .read(permissionEntity.getRead_())
                .create(permissionEntity.getCreate_())
                .update(permissionEntity.getUpdate_())
                .delete(permissionEntity.getDelete_())
                .build();
    }

}
