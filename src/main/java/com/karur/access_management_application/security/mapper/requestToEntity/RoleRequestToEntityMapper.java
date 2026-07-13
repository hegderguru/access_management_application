package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AuthorityRoleEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.model.request.RoleRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleRequestToEntityMapper {

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
}
