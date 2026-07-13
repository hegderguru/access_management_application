package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestToEntityMapper {

    @Autowired
    AccessRequestToEntityMapper accessRequestToEntityMapper;

    public AccessEntity buildAccessEntity(AccessRequest accessRequest) {
        AccessEntity accessEntity = accessRequestToEntityMapper.buildAccessEntity(accessRequest);
        accessRequest.getAuthorityRequests().forEach(authorityRequest -> {
            AuthorityEntity authorityEntity = buildAuthorityEntity(authorityRequest);
            accessEntity.getAuthorityEntities().add(authorityEntity);
            authorityRequest.getRoleRequests().forEach(roleRequest -> {
                RoleEntity roleEntity = buildRoleEntity(roleRequest);
                authorityEntity.getRoleEntities().add(roleEntity);
                roleRequest.getPermissionRequests().forEach(permissionRequest -> {
                    roleEntity.getPermissionEntities().add(buildPermissionEntity(permissionRequest));
                });
            });
        });
        return accessEntity;
    }

    public AuthorityEntity buildAuthorityEntity(AuthorityRequest authorityRequest) {
        return accessRequestToEntityMapper.buildAuthorityEntity(authorityRequest);
    }

    public RoleEntity buildRoleEntity(RoleRequest roleRequest) {
        return accessRequestToEntityMapper.buildRoleEntity(roleRequest);
    }

    public PermissionEntity buildPermissionEntity(PermissionRequest permissionRequest) {
        return accessRequestToEntityMapper.buildPermissionEntity(permissionRequest);
    }

}
