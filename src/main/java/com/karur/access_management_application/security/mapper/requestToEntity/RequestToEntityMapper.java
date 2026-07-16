package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.mapper.entityToRead.EntityToReadMapper;
import com.karur.access_management_application.security.mapper.entiyToRequest.EntityToAccessReuestMapper;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import com.karur.access_management_application.security.util.AccessRequestUpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
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
    @Autowired
    AccessRepository accessRepository;

    @Autowired
    EntityToAccessReuestMapper entityToAccessReuestMapper;

    @Autowired
    EntityToReadMapper entityToReadMapper;

    public Mono<AccessEntity> saveOrUpdateAccess(AccessRequest accessRequest) {
        return accessRepository.fetchAccessEntity(accessRequest.getUsername())
                .doOnSuccess(accessEntity -> log.info("Fetched Access Entity: {}", accessEntity))
                .switchIfEmpty(Mono.error(new IllegalAccessError("User not found")))
                .flatMap(accessEntity -> {
                    List<CompareUtil.Change> changes = AccessRequestUpdateUtil
                            .accessUpdateChanges(entityToAccessReuestMapper.buildAccessRequest(accessEntity), accessRequest);
                    return accessRequestToEntityMapper.updateAccessOnChanges(accessEntity, changes)
                            .then(Mono.defer(() -> saveOrUpdateAuthorities(accessEntity, changes)))
                            .then(Mono.just(accessEntity));
                })
                .flatMap(accessEntity -> accessRepository.saveAccessEntity(accessEntity));
    }


    public @NonNull Mono<AccessEntity> saveOrUpdateAuthorities(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return authorityRequestToEntityMapper.saveOrUpdateAuthoritiesOnChanges(accessEntity, changes)
                .thenMany(Flux.defer(() -> Flux.fromIterable(accessEntity.getAuthorityEntities())))
                .flatMap(authorityEntity -> saveOrUpdateRoles(authorityEntity, changes)
                ).then(Mono.just(accessEntity));
    }

    private @NonNull Flux<Void> saveOrUpdateRoles(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        return roleRequestToEntityMapper.saveOrUpdateRolesOnChanges(authorityEntity, changes)
                .thenMany(Flux.defer(() -> Flux.fromIterable(authorityEntity.getRoleEntities())))
                .flatMap(roleEntity -> permissionRequestToEntityMapper.saveOrUpdatePermissionsOnChanges(roleEntity, changes));
    }

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

    //Create ->
    public AccessEntity buildOnlyAccessEntity(AccessRequest accessRequest) {
        return accessRequestToEntityMapper.buildAccessEntity(accessRequest);
    }

    public AuthorityEntity buildOnlyAuthorityEntity(AuthorityRequest authorityRequest) {
        AuthorityEntity authorityEntity = authorityRequestToEntityMapper.buildAuthorityEntity(authorityRequest);
        authorityRequest.getRoleRequests().forEach(roleRequest -> {
            authorityEntity.addRoleEntity(roleRequestToEntityMapper.buildRoleEntity(roleRequest));
        });
        return authorityEntity;
    }

    public RoleEntity buildOnlyRoleEntity(RoleRequest roleRequest) {
        RoleEntity roleEntity = roleRequestToEntityMapper.buildRoleEntity(roleRequest);
        roleRequest.getPermissionRequests().forEach(permissionRequest -> {
            roleEntity.addPermissionEntity(buildPermissionEntity(permissionRequest));
        });
        return roleEntity;
    }
    //Create ->


}
