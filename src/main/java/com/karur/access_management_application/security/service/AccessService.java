package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.compare.ChangeUtil;
import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.mapper.requestToEntity.EntityToAccessReuestMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.EntityToReadMapper;
import com.karur.access_management_application.security.mapper.requestToEntity.RequestToEntityMapper;
import com.karur.access_management_application.security.model.read.AccessDetail;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import com.karur.access_management_application.security.util.AccessDetailsUpdateUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Mono<AccessDetail> saveOrUpdateAccess(AccessRequest accessRequest) {
        return accessRepository.fetchAccessEntity(accessRequest.getUsername())
                .switchIfEmpty(Mono.just(requestToEntityMapper.buildAccessEntity(accessRequest)))
                .flatMap(accessEntity -> {
                    List<CompareUtil.Change> changes = AccessDetailsUpdateUtil.accessChanges(entityToAccessReuestMapper.buildAccessRequest(accessEntity), accessRequest);
                    return updateAccessOnChanges(accessEntity, changes)
                            .then(Mono.defer(() -> saveOrUpdateAuthorities(accessEntity, changes))).thenReturn(accessEntity);
                }).map(entityToReadMapper::buildAccessDetail);
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
        return newAuthoritiesOnChanges(accessEntity, changes).flatMap(unused -> updateAuthoritiesOnChanges(accessEntity, changes));
    }

    public Mono<Void> saveOrUpdateRolesOnChanges(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        return newRoleEntitiesOnChanges(authorityEntity, changes).flatMap(unused -> updateRoleEntitiesOnChanges(authorityEntity, changes));
    }

    public Mono<Void> saveOrUpdatePermissionsOnChanges(RoleEntity roleEntity, List<CompareUtil.Change> changes) {
        return newPermissionEntitiesOnChanges(roleEntity, changes).flatMap(unused -> updatePermissionEntitiesOnChanges(roleEntity, changes));
    }


    //////////////////
    private Mono<Void> updatePermissionEntitiesOnChanges(RoleEntity roleEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getUpdatePermissionRequest(changes))
                .flatMap(change -> {
                    PermissionRequest permissionRequest = (PermissionRequest) change.getRight();
                    PermissionEntity permissionEntity = roleEntity.getPermissionEntities().stream().filter(permissionEntity1 -> permissionEntity1.fullyQualifiedFieldPath().equalsIgnoreCase(permissionRequest.fullyQualifiedClassPath())).findFirst().get();
                    switch (PermissionRequest.Fields.valueOf(change.getField().getName())) {
                        case read -> permissionEntity.setRead(ChangeUtil.getBooleanElseConvert(change));
                        case create -> permissionEntity.setCreate(ChangeUtil.getBooleanElseConvert(change));
                        case update -> permissionEntity.setUpdate(ChangeUtil.getBooleanElseConvert(change));
                        case delete -> permissionEntity.setDelete(ChangeUtil.getBooleanElseConvert(change));
                    }
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> newPermissionEntitiesOnChanges(RoleEntity roleEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getNewPermissionRequest(changes))
                .flatMap(change -> Mono.just(requestToEntityMapper.buildPermissionEntity((PermissionRequest) change.getRightValue())))
                .flatMap(permissionEntity -> {
                    roleEntity.getPermissionEntities().add(permissionEntity);
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> updateRoleEntitiesOnChanges(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getUpdateRoleRequest(changes))
                .flatMap(change -> {
                    RoleRequest roleRequest = (RoleRequest) change.getRight();
                    RoleEntity roleEntity = authorityEntity.getRoleEntities().stream().filter(roleEntity1 -> roleEntity1.getName().equalsIgnoreCase(roleRequest.getName())).findFirst().get();
                    switch (RoleRequest.Fields.valueOf(change.getField().getName())) {
                        case description -> authorityEntity.setName(ChangeUtil.getStringElseConvert(change));
                    }
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> newRoleEntitiesOnChanges(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getNewRoleRequest(changes))
                .flatMap(change -> Mono.just(requestToEntityMapper.buildRoleEntity((RoleRequest) change.getRightValue())))
                .flatMap(roleEntity -> {
                    authorityEntity.getRoleEntities().add(roleEntity);
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> newAuthoritiesOnChanges(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getNewAuthorityRequest(changes))
                .flatMap(change -> Mono.just(requestToEntityMapper.buildAuthorityEntity((AuthorityRequest) change.getRightValue())))
                .flatMap(authorityEntity -> {
                    accessEntity.getAuthorityEntities().add(authorityEntity);
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> updateAuthoritiesOnChanges(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getUpdateAuthorityRequest(changes))
                .flatMap(change -> {
                    AuthorityRequest authorityRequest = (AuthorityRequest) change.getRight();
                    AuthorityEntity authorityEntity = accessEntity.getAuthorityEntities().stream().filter(authorityEntity1 -> authorityEntity1.getName().equalsIgnoreCase(authorityRequest.getName())).findFirst().get();
                    switch (AuthorityRequest.Fields.valueOf(change.getField().getName())) {
                        case description -> authorityEntity.setName(ChangeUtil.getStringElseConvert(change));
                    }
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> updateAccessOnChanges(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getUpdateAccessRequest(changes))
                .flatMap(change -> {
                    switch (AccessRequest.Fields.valueOf(change.getField().getName())) {
                        case firstName -> accessEntity.setFirstName(ChangeUtil.getStringElseConvert(change));
                        case middleName -> accessEntity.setMiddleName(ChangeUtil.getStringElseConvert(change));
                        case lastName -> accessEntity.setLastName(ChangeUtil.getStringElseConvert(change));
                        case accessEnabled -> accessEntity.setAccessEnabled(ChangeUtil.getBooleanElseConvert(change));
                        case accessLocked -> accessEntity.setAccessLocked(ChangeUtil.getBooleanElseConvert(change));
                        case accessExpired -> accessEntity.setAccessExpired(ChangeUtil.getBooleanElseConvert(change));
                        case credentialsExpired ->
                                accessEntity.setCredentialsExpired(ChangeUtil.getBooleanElseConvert(change));
                    }
                    return Mono.empty();
                }).then();
    }
}
