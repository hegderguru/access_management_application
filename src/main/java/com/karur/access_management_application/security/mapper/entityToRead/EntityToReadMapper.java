package com.karur.access_management_application.security.mapper.entityToRead;

import com.karur.access_management_application.security.authentication.model.UserDetail;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.model.read.*;
import com.karur.access_management_application.security.repository.AccessRepository;
import com.karur.access_management_application.security.util.CommonUtil;
import com.karur.access_management_application.validate.annotation.ValidateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class EntityToReadMapper {

    @Autowired
    AccessRepository accessRepository;

    public Mono<UserDetail> buildUserDetail(String username) {
        return accessRepository.fetchAccessEntity(username)
                .map(this::buildAccessDetail).map(UserDetail::new);
    }

    public Mono<AccessDetail> buildAccessDetail(String username) {
        return accessRepository.fetchAccessEntity(username)
                .map(this::buildAccessDetail);
    }

    public AccessDetail buildAccessDetail(AccessEntity accessEntity) {
        return AccessDetail.builder()
                .username(accessEntity.getUsername())
                .password(accessEntity.getPassword())
                .firstName(accessEntity.getFirstName())
                .middleName(accessEntity.getMiddleName())
                .lastName(accessEntity.getLastName())
                .accessEnabled(accessEntity.isAccessEnabled())
                .accessLocked(accessEntity.isAccessLocked())
                .accessExpired(accessEntity.isAccessExpired())
                .credentialsExpired(accessEntity.isCredentialsExpired())
                .authorityDetails(buildAuthorityDetails(accessEntity.accessGrantedAuthorities()))
                .build();
    }

    public List<AuthorityDetail> buildAuthorityDetails(List<AuthorityEntity> accessGrantedAuthorityEntities) {
        return CommonUtil.returnListElseEmpty(accessGrantedAuthorityEntities).stream().map(this::buildAuthorityDetail).toList();
    }

    public AuthorityDetail buildAuthorityDetail(AuthorityEntity authorityEntity) {
        return AuthorityDetail.builder()
                .name(authorityEntity.getName())
                .description(authorityEntity.getDescription())
                .roleDetails(buildRoleDetails(authorityEntity.getRoleEntities()))
                .build();
    }

    private List<RoleDetail> buildRoleDetails(List<RoleEntity> accessRoleEntities) {
        return CommonUtil.returnListElseEmpty(accessRoleEntities).stream().map(this::buildRoleDetail).toList();
    }

    public RoleDetail buildRoleDetail(RoleEntity roleEntity) {
        return RoleDetail.builder()
                .name(roleEntity.getName())
                .description(roleEntity.getDescription())
                .permissionDetails(buildPermissionsDetails(roleEntity.getPermissionEntities()))
                .build();
    }

    private List<PermissionDetail> buildPermissionsDetails(List<PermissionEntity> accessPermissionEntities) {
        return CommonUtil.returnListElseEmpty(accessPermissionEntities).stream().map(this::buildPermissionDetail).toList();
    }

    private PermissionDetail buildPermissionDetail(PermissionEntity permissionEntity) {
        return PermissionDetail.builder()
                .classPath(permissionEntity.getClassPath())
                .className(permissionEntity.getClassName())
                .fieldName(permissionEntity.getFieldName())
                .read(permissionEntity.isRead())
                .create(permissionEntity.isCreate())
                .update(permissionEntity.isUpdate())
                .delete(permissionEntity.isDelete())
                .build();
    }

}
