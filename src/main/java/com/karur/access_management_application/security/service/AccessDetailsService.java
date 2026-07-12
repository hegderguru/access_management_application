package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.mapper.requestToEntity.EntityToReadMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.RequestToEntityMapper;
import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Data
@Service
public class AccessDetailsService implements ReactiveUserDetailsService {

    @Autowired
    AccessRepository accessRepository;

    @Autowired
    RequestToEntityMapper requestToEntityMapper;

    @Autowired
    EntityToReadMapper entityToReadMapper;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accessRepository.findAccessEntityByUsername(username).flatMap(accessEntity -> Mono.just((UserDetails) accessEntity));
    }

    public Mono<AccessDetail> fetchOnlyAccessDetails(String username) {
        return accessRepository.fetchOnlyAccessEntity(username).flatMap(accessEntity -> Mono.just(entityToReadMapper.buildAccessDetail(accessEntity)));
    }

    public Mono<AccessDetail> fetchAuthorityDetails(String username) {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
                .map(authentication -> authentication.getPrincipal().toString())
                .filter(username::equalsIgnoreCase)
                .flatMap(un -> entityToReadMapper.buildAccessDetail(un)
                        .map(accessDetail -> AccessDetail.builder().username(un).authorityDetails(accessDetail.getAuthorityDetails()).build()));
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