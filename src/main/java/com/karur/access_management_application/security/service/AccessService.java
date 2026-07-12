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


    private Mono<Void> updatePermissionEntities(RoleEntity roleEntity, List<CompareUtil.Change> changes) {
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

    private Mono<Void> newPermissionEntities(RoleEntity roleEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getNewPermissionRequest(changes))
                .flatMap(change -> Mono.just(requestToEntityMapper.buildPermissionEntity((PermissionRequest) change.getRightValue())))
                .flatMap(permissionEntity -> {
                    roleEntity.getPermissionEntities().add(permissionEntity);
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> updateRoleEntities(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
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

    private Mono<Void> newRoleEntities(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getNewRoleRequest(changes))
                .flatMap(change -> Mono.just(requestToEntityMapper.buildRoleEntity((RoleRequest) change.getRightValue())))
                .flatMap(roleEntity -> {
                    authorityEntity.getRoleEntities().add(roleEntity);
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> newAuthorities(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessDetailsUpdateUtil.getNewAuthorityRequest(changes))
                .flatMap(change -> Mono.just(requestToEntityMapper.buildAuthorityEntity((AuthorityRequest) change.getRightValue())))
                .flatMap(authorityEntity -> {
                    accessEntity.getAuthorityEntities().add(authorityEntity);
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> updateAuthorities(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
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
}
