package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.mapper.requestToEntity.RequestToEntityMapper;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Data
@Service
public class AccessorDetailsService implements ReactiveUserDetailsService {

    @Autowired
    AccessRepository accessRepository;

    @Autowired
    RequestToEntityMapper requestToEntityMapper;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accessRepository.findAccessEntityByUsername(username).flatMap(accessEntity -> Mono.just((UserDetails) accessEntity));
    }

    public Mono<AccessEntity> createAccessEntity(AccessRequest accessRequest) {
        return accessRepository.saveAccessEntity(requestToEntityMapper.buildAccessEntity(accessRequest));
    }

    public Mono<AuthorityEntity> createAuthority(AuthorityRequest authorityRequest) {
        return accessRepository.saveAuthorityEntity(requestToEntityMapper.buildAuthorityEntity(authorityRequest));
    }

    public Mono<RoleEntity> createRole(RoleRequest roleRequest) {
        return accessRepository.saveRoleEntity(requestToEntityMapper.buildRoleEntity(roleRequest));
    }

    public Mono<PermissionEntity> createPermission(PermissionRequest permissionRequest) {
        return accessRepository.savePermissionEntity(requestToEntityMapper.buildPermissionEntity(permissionRequest));
    }
}