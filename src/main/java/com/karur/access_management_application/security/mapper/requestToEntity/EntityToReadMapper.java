package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.authentication.provider.JwtTokenProvider;
import com.karur.access_management_application.security.authentication.model.AuthorityEntity;
import com.karur.access_management_application.security.authentication.model.PermissionEntity;
import com.karur.access_management_application.security.authentication.model.RoleEntity;
import com.karur.access_management_application.security.authentication.model.AccessEntity;
import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.read.AuthorityDetail;
import com.karur.access_management_application.security.model.read.PermissionDetail;
import com.karur.access_management_application.security.model.read.RoleDetail;
import com.karur.access_management_application.security.repository.AccessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class EntityToReadMapper {

    @Autowired
    AccessorRepository accessorRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public Mono<AccessDetail> buildAccessDetail(String username) {
        return accessorRepository.findAccessorEntityByUsername(username)
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
        return accessGrantedAuthorityEntities.stream().map(this::buildAuthorityDetail).toList();
    }

    public AuthorityDetail buildAuthorityDetail(AuthorityEntity authorityEntity) {
        return AuthorityDetail.builder()
                .name(authorityEntity.getName())
                .description(authorityEntity.getDescription())
                .roleDetails(buildRoleDetails(authorityEntity.getAccessRoleEntities()))
                .build();
    }

    private List<RoleDetail> buildRoleDetails(List<RoleEntity> accessRoleEntities) {
        return accessRoleEntities.stream().map(this::buildRoleDetail).toList();
    }

    private RoleDetail buildRoleDetail(RoleEntity roleEntity) {
        return RoleDetail.builder()
                .name(roleEntity.getName())
                .description(roleEntity.getDescription())
                .permissionDetails(buildPermissionsDetails(roleEntity.getAccessPermissionEntities()))
                .build();
    }

    private List<PermissionDetail> buildPermissionsDetails(List<PermissionEntity> accessPermissionEntities) {
        return accessPermissionEntities.stream().map(this::buildPermissionDetail).toList();
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

    public String generateJwtToken(String subject, Map<String, Object> claims){
        return jwtTokenProvider.generateToken(subject,claims);
    }
}
