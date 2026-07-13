package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.join.AuthorityRoleEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.model.request.RoleRequest;
import org.springframework.stereotype.Service;

@Service
public class RoleRequestToEntityMapper {

    public AuthorityRoleEntity buildAuthorityRoleEntity(Long authorityId, RoleEntity roleEntity) {
        return AuthorityRoleEntity.builder()
                .authorityId(authorityId)
                .roleId(roleEntity.getId())
                .build();
    }

    public RoleEntity buildRoleEntity(RoleRequest roleRequest) {
        return RoleEntity.builder()
                .name(roleRequest.getName())
                .description(roleRequest.getDescription())
                .build();
    }
}
