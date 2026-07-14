package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.entity.join.AccessAuthorityEntity;
import com.karur.access_management_application.security.entity.join.AuthorityRoleEntity;
import com.karur.access_management_application.security.entity.join.RolePermissionEntity;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestToEntityMapper {

    @Autowired
    AccessRequestToEntityMapper accessRequestToEntityMapper;

    @Autowired
    AuthorityRequestToEntityMapper authorityRequestToEntityMapper;

    @Autowired
    RoleRequestToEntityMapper roleRequestToEntityMapper;

    @Autowired
    PermissionRequestToEntityMapper permissionRequestToEntityMapper;

    public AccessEntity buildAccessEntity(AccessRequest accessRequest) {
        AccessEntity accessEntity = accessRequestToEntityMapper.buildAccessEntity(accessRequest);
        accessRequest.getAuthorityRequests().forEach(authorityRequest -> {
            AuthorityEntity authorityEntity = buildAuthorityEntity(authorityRequest);
            accessEntity.addAuthorityEntity(authorityEntity);
        });
        return accessEntity;
    }

    public AuthorityEntity buildAuthorityEntity(AuthorityRequest authorityRequest) {
        AuthorityEntity authorityEntity = authorityRequestToEntityMapper.buildAuthorityEntity(authorityRequest);
        authorityRequest.getRoleRequests().forEach(roleRequest -> {
            RoleEntity roleEntity = buildRoleEntity(roleRequest);
            authorityEntity.addRoleEntity(roleEntity);
        });
        return authorityEntity;
    }

    public RoleEntity buildRoleEntity(RoleRequest roleRequest) {
        RoleEntity roleEntity = roleRequestToEntityMapper.buildRoleEntity(roleRequest);
        roleRequest.getPermissionRequests().forEach(permissionRequest -> {
            roleEntity.addPermissionEntity(buildPermissionEntity(permissionRequest));
        });
        return roleEntity;
    }

    public PermissionEntity buildPermissionEntity(PermissionRequest permissionRequest) {
        return permissionRequestToEntityMapper.buildPermissionEntity(permissionRequest);
    }

    public AccessEntity buildOnlyAccessEntity(AccessRequest accessRequest) {
        return accessRequestToEntityMapper.buildAccessEntity(accessRequest);
    }

    public AuthorityEntity buildOnlyAuthorityEntity(AuthorityRequest authorityRequest) {
        return authorityRequestToEntityMapper.buildAuthorityEntity(authorityRequest);
    }

    public RoleEntity buildOnlyRoleEntity(RoleRequest roleRequest) {
        return roleRequestToEntityMapper.buildRoleEntity(roleRequest);
    }

    public PermissionEntity buildOnlyPermissionEntity(PermissionRequest permissionRequest) {
        return permissionRequestToEntityMapper.buildPermissionEntity(permissionRequest);
    }

    public List<AccessAuthorityEntity> buildAccessAuthorityEntity(AccessEntity accessEntity, List<AuthorityEntity> authorityEntities) {
        return authorityEntities.stream().map(authorityEntity -> authorityRequestToEntityMapper.buildAccessAuthorityEntity(accessEntity.getId(), authorityEntity)).toList();
    }

    public List<AuthorityRoleEntity> buildAuthorityRoleEntity(AuthorityEntity authorityEntity, List<RoleEntity> roleEntities) {
        return roleEntities.stream().map(roleEntity -> roleRequestToEntityMapper.buildAuthorityRoleEntity(authorityEntity.getId(), roleEntity)).toList();
    }

    public List<RolePermissionEntity> buildRolePermissionEntity(RoleEntity roleEntity, List<PermissionEntity> permissionEntities) {
        return permissionEntities.stream().map(permissionEntity -> permissionRequestToEntityMapper.buildRolePermissionEntity(roleEntity.getId(), permissionEntity)).toList();
    }

}
