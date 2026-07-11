package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.authentication.model.AccessGrantedAuthorityEntity;
import com.karur.access_management_application.security.authentication.model.AccessPermissionEntity;
import com.karur.access_management_application.security.authentication.model.AccessRoleEntity;
import com.karur.access_management_application.security.authentication.model.AccessorEntity;
import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.read.AuthorityDetail;
import com.karur.access_management_application.security.model.read.PermissionDetail;
import com.karur.access_management_application.security.model.read.RoleDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntityToReadMapper {

    public AccessDetail buildAccessDetail(AccessorEntity accessorEntity) {
        return AccessDetail.builder()
                .username(accessorEntity.getUsername())
                .password(accessorEntity.getPassword())
                .firstName(accessorEntity.getFirstName())
                .middleName(accessorEntity.getMiddleName())
                .lastName(accessorEntity.getLastName())
                .accessEnabled(accessorEntity.isAccessEnabled())
                .accessLocked(accessorEntity.isAccessLocked())
                .accessExpired(accessorEntity.isAccessExpired())
                .credentialsExpired(accessorEntity.isCredentialsExpired())
                .authorityDetails(buildAuthorityDetails(accessorEntity.accessGrantedAuthorities()))
                .build();
    }

    public List<AuthorityDetail> buildAuthorityDetails(List<AccessGrantedAuthorityEntity> accessGrantedAuthorityEntities) {
        return accessGrantedAuthorityEntities.stream().map(this::buildAuthorityDetail).toList();
    }

    public AuthorityDetail buildAuthorityDetail(AccessGrantedAuthorityEntity accessGrantedAuthorityEntity) {
        return AuthorityDetail.builder()
                .name(accessGrantedAuthorityEntity.getName())
                .description(accessGrantedAuthorityEntity.getDescription())
                .roleDetails(buildRoleDetails(accessGrantedAuthorityEntity.getAccessRoleEntities()))
                .build();
    }

    private List<RoleDetail> buildRoleDetails(List<AccessRoleEntity> accessRoleEntities) {
        return accessRoleEntities.stream().map(this::buildRoleDetail).toList();
    }

    private RoleDetail buildRoleDetail(AccessRoleEntity accessRoleEntity) {
        return RoleDetail.builder()
                .name(accessRoleEntity.getName())
                .description(accessRoleEntity.getDescription())
                .permissionDetails(buildPermissionsDetails(accessRoleEntity.getAccessPermissionEntities()))
                .build();
    }

    private List<PermissionDetail> buildPermissionsDetails(List<AccessPermissionEntity> accessPermissionEntities) {
        return accessPermissionEntities.stream().map(this::buildPermissionDetail).toList();
    }

    private PermissionDetail buildPermissionDetail(AccessPermissionEntity accessPermissionEntity) {
        return PermissionDetail.builder()
                .classPath(accessPermissionEntity.getClassPath())
                .className(accessPermissionEntity.getClassName())
                .fieldName(accessPermissionEntity.getFieldName())
                .read(accessPermissionEntity.isRead())
                .create(accessPermissionEntity.isCreate())
                .update(accessPermissionEntity.isUpdate())
                .delete(accessPermissionEntity.isDelete())
                .build();
    }
}
