package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.compare.ChangeUtil;
import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.mapper.entiyToRequest.EntityToAccessReuestMapper;
import com.karur.access_management_application.security.mapper.entityToRead.EntityToReadMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.*;
import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import com.karur.access_management_application.security.util.AccessRequestUpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AccessService {

    @Autowired
    AccessRepository accessRepository;

    @Autowired
    RequestToEntityMapper requestToEntityMapper;

    @Autowired
    EntityToAccessReuestMapper entityToAccessReuestMapper;

    @Autowired
    EntityToReadMapper entityToReadMapper;

    @Autowired
    AccessRequestToEntityMapper accessRequestToEntityMapper;

    @Autowired
    AuthorityRequestToEntityMapper authorityRequestToEntityMapper;

    @Autowired
    RoleRequestToEntityMapper roleRequestToEntityMapper;

    @Autowired
    PermissionRequestToEntityMapper permissionRequestToEntityMapper;

    public Mono<AccessDetail> fetchAccessDetails(String username) {
        return accessRepository.fetchAccessEntity(username).flatMap(accessEntity -> Mono.just(entityToReadMapper.buildAccessDetail(accessEntity)));
    }

    public Mono<AccessDetail> fetchAuthorityDetails(String username) {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
                .map(authentication -> authentication.getPrincipal().toString())
                .filter(username::equalsIgnoreCase)
                .flatMap(un -> entityToReadMapper.buildAccessDetail(un)
                        .map(accessDetail -> AccessDetail.builder().username(un).authorities(accessDetail.getAuthorities()).build()));
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

    public Mono<AccessDetail> saveOrUpdateAccess(AccessRequest accessRequest) {
        return accessRepository.fetchAccessEntity(accessRequest.getUsername())
                .doOnSuccess(accessEntity -> log.info("Fetched Access Entity: {}", accessEntity))
                .switchIfEmpty(Mono.defer(() -> Mono.just(requestToEntityMapper.buildAccessEntity(accessRequest))))
                .flatMap(accessEntity -> {
                    List<CompareUtil.Change> changes = AccessRequestUpdateUtil
                            .accessChanges(entityToAccessReuestMapper.buildAccessRequest(accessEntity), accessRequest);

                    return accessRequestToEntityMapper.updateAccessOnChanges(accessEntity, changes)
                            .then(Mono.defer(() -> saveOrUpdateAuthorities(accessEntity, changes)))
                            .then(Mono.just(accessEntity));
                })
                .flatMap(accessEntity -> accessRepository.saveAccessEntity(accessEntity))
                .map(entityToReadMapper::buildAccessDetail);
    }


    public @NonNull Mono<AccessEntity> saveOrUpdateAuthorities(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return saveOrUpdateAuthoritiesOnChanges(accessEntity, changes)
                .thenMany(Flux.defer(() -> Flux.fromIterable(accessEntity.getAuthorityEntities())))
                .flatMap(authorityEntity -> saveOrUpdateRoles(authorityEntity, changes)
                ).then(Mono.just(accessEntity));
    }

    private @NonNull Flux<Void> saveOrUpdateRoles(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        return saveOrUpdateRolesOnChanges(authorityEntity, changes)
                .thenMany(Flux.defer(() -> Flux.fromIterable(authorityEntity.getRoleEntities())))
                .flatMap(roleEntity -> saveOrUpdatePermissionsOnChanges(roleEntity, changes));
    }

    public Mono<Void> saveOrUpdateAuthoritiesOnChanges(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return authorityRequestToEntityMapper.newAuthoritiesOnChanges(accessEntity, changes).then(Mono.defer(() -> authorityRequestToEntityMapper.updateAuthoritiesOnChanges(accessEntity, changes)));
    }

    public Mono<Void> saveOrUpdateRolesOnChanges(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        return roleRequestToEntityMapper.newRolesOnChanges(authorityEntity, changes).then(Mono.defer(() -> roleRequestToEntityMapper.updateRolesOnChanges(authorityEntity, changes)));
    }

    public Mono<Void> saveOrUpdatePermissionsOnChanges(RoleEntity roleEntity, List<CompareUtil.Change> changes) {
        return permissionRequestToEntityMapper.newPermissionsOnChanges(roleEntity, changes).then(Mono.defer(() -> permissionRequestToEntityMapper.updatePermissionsOnChanges(roleEntity, changes)));
    }


}
