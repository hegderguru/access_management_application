package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.model.request.AccessorRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestToEntityMapper {

    @Autowired
    AccessRequestToEntityMapper accessRequestToEntityMapper;

    @Autowired
    AccessAuthorityRequestToEntityMapper accessAuthorityRequestToEntityMapper;

    public AccessEntity buildAccessorEntity(AccessorRequest accessorRequest) {
        AccessEntity accessEntity = accessRequestToEntityMapper.buildAccessorEntity(accessorRequest);
        accessEntity.setAccessGrantedAuthorities(accessAuthorityRequestToEntityMapper.buildAccessGrantedAuthorityEntities(accessorRequest));
        return accessEntity;
    }

    public AuthorityEntity buildAccessGrantedAuthorityEntity(AuthorityRequest authorityRequest) {
        return accessAuthorityRequestToEntityMapper.buildAccessGrantedAuthorityEntity(authorityRequest);
    }

    public RoleEntity buildRoleEntity(RoleRequest roleRequest) {
        return accessRequestToEntityMapper.buildRoleEntity(roleRequest);
    }

    public PermissionEntity buildPermissionEntity(PermissionRequest permissionRequest) {
        return accessRequestToEntityMapper.buildPermissionEntity(permissionRequest);
    }
}
