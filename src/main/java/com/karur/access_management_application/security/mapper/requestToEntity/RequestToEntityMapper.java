package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.mapper.entiyToRequest.EntityToAccessReuestMapper;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import com.karur.access_management_application.security.util.AccessRequestUpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    //Update
    public Mono<AccessEntity> updateAccess(AccessRequest accessRequest) {
        return accessRepository.fetchAccessEntity(accessRequest.getUsername())
                .switchIfEmpty(Mono.error(new IllegalAccessError("User not found")))
                .flatMap(accessEntity -> {
                    List<CompareUtil.Change> changes = AccessRequestUpdateUtil
                            .accessUpdateChanges(entityToAccessReuestMapper.buildAccessRequest(accessEntity), accessRequest);
                    return accessRequestToEntityMapper.updateAccessOnChanges(accessEntity, changes)
                            .then(Mono.just(accessEntity));
                });
    }

    public Mono<AuthorityEntity> updateAuthority(AuthorityRequest authorityRequest) {
        return accessRepository.fetchAuthorityEntity(authorityRequest.getName())
                .switchIfEmpty(Mono.error(new IllegalAccessError("Authority not found")))
                .flatMap(authorityEntity -> {
                    List<CompareUtil.Change> changes = AccessRequestUpdateUtil
                            .accessUpdateChanges(entityToAccessReuestMapper.buildAuthorityRequest(authorityEntity), authorityRequest);
                    return authorityRequestToEntityMapper.updateAuthorityOnChanges(authorityEntity, changes)
                            .then(Mono.just(authorityEntity));
                })
                .flatMap(authorityEntity -> accessRepository.saveAuthorityEntity(authorityEntity));
    }

    public Mono<RoleEntity> updateRole(RoleRequest roleRequest) {
        return accessRepository.fetchRoleEntity(roleRequest.getName())
                .switchIfEmpty(Mono.error(new IllegalAccessError("Role not found")))
                .flatMap(roleEntity -> {
                    List<CompareUtil.Change> changes = AccessRequestUpdateUtil
                            .accessUpdateChanges(entityToAccessReuestMapper.buildRoleRequest(roleEntity), roleRequest);
                    return roleRequestToEntityMapper.updateRoleOnChanges(roleEntity, changes)
                            .then(Mono.just(roleEntity));
                })
                .flatMap(authorityEntity -> accessRepository.saveRoleEntity(authorityEntity));
    }

    //Update

}
