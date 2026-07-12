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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccessorDetailsService implements ReactiveUserDetailsService {

    @Autowired
    AccessRepository accessRepository;

    @Autowired
    RequestToEntityMapper requestToEntityMapper;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accessRepository.findAccessorEntityByUsername(username).flatMap(accessorEntity -> Mono.just((UserDetails) accessorEntity));
    }

    public Mono<AccessEntity> createAccessEntity(AccessRequest accessRequest) {
        AccessEntity accessEntity = requestToEntityMapper.buildAccessorEntity(accessRequest);
        return accessRepository.save(accessEntity);
    }

    public Mono<AuthorityEntity> createAuthority(AuthorityRequest authorityRequest) {
        AuthorityEntity authorityEntity = requestToEntityMapper.buildAccessGrantedAuthorityEntity(authorityRequest);
        return accessRepository.save(authorityEntity);
    }

    public Mono<RoleEntity> createRole(RoleRequest roleRequest) {
        RoleEntity roleEntity = requestToEntityMapper.buildRoleEntity(roleRequest);
        return accessRepository.save(roleEntity);
    }

    public Mono<PermissionEntity> createPermission(PermissionRequest permissionRequest) {
        PermissionEntity permissionEntity = requestToEntityMapper.buildPermissionEntity(permissionRequest);
        return accessRepository.save(permissionEntity);
    }
}
