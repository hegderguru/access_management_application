package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.repository.AccessRepository;
import com.karur.access_management_application.security.repository.inter.joinTable.AccessAuthorityIdRepository;
import com.karur.access_management_application.security.repository.inter.joinTable.AuthorityRoleIdRepository;
import com.karur.access_management_application.security.repository.inter.joinTable.RolePermissionIdRepository;
import com.karur.access_management_application.security.repository.inter.table.AccessEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.AuthorityEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.PermissionEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.RoleEntityRepository;
import com.karur.access_management_application.security.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
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
    AccessEntityRepository accessEntityRepository;

    @Autowired
    AuthorityEntityRepository authorityEntityRepository;

    @Autowired
    RoleEntityRepository roleEntityRepository;

    @Autowired
    PermissionEntityRepository permissionEntityRepository;

    @Autowired
    AccessAuthorityIdRepository accessAuthorityIdRepository;

    @Autowired
    AuthorityRoleIdRepository authorityRoleIdRepository;

    @Autowired
    RolePermissionIdRepository rolePermissionIdRepository;

    @Autowired
    AccessRepository accessRepository;

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

    /*New Implementation*/

    public Mono<AccessEntity> buildAccessEntity1(AccessRequest accessRequest) {
        return accessRepository.fetchAccessEntity(accessRequest.getUsername())
                .switchIfEmpty(Mono.defer(() -> Mono.just(accessRequestToEntityMapper.buildAccessEntity(accessRequest))))
                .flatMap(accessEntity -> Flux.fromIterable(CommonUtil.returnListElseEmpty(accessRequest.getAuthorityRequests()))
                        .flatMap(authorityRequest -> buildAuthorityEntity1(authorityRequest, accessEntity.getAuthorityEntities()))
                        .collectList()
                        .map(authorityEntities -> {
                            authorityEntities.forEach(accessEntity::addAuthorityEntity);
                            return accessEntity;
                        })
                );
    }

    public Mono<AuthorityEntity> buildAuthorityEntity1(AuthorityRequest authorityRequest, List<AuthorityEntity> authorityEntities) {
        return Flux.fromIterable(CommonUtil.returnListElseEmpty(authorityEntities)).filter(authorityEntity -> authorityEntity.getName().equalsIgnoreCase(authorityRequest.getName()))
                .next()
                .switchIfEmpty(Mono.defer(() -> Mono.just(authorityRequestToEntityMapper.buildAuthorityEntity(authorityRequest))))
                .flatMap(authorityEntity -> Flux.fromIterable(CommonUtil.returnListElseEmpty(authorityRequest.getRoleRequests()))
                        .flatMap(roleRequest -> buildRoleEntity1(roleRequest, authorityEntity.getRoleEntities()))
                        .collectList()
                        .map(roleEntities -> {
                            roleEntities.forEach(authorityEntity::addRoleEntity);
                            return authorityEntity;
                        })
                );
    }

    public Mono<RoleEntity> buildRoleEntity1(RoleRequest roleRequest, List<RoleEntity> roleEntities) {
        return Flux.fromIterable(CommonUtil.returnListElseEmpty(roleEntities)).filter(roleEntity -> roleEntity.getName().equalsIgnoreCase(roleRequest.getName()))
                .next()
                .switchIfEmpty(Mono.defer(() -> Mono.just(roleRequestToEntityMapper.buildRoleEntity(roleRequest))))
                .flatMap(roleEntity -> Flux.fromIterable(CommonUtil.returnListElseEmpty(roleRequest.getPermissionRequests()))
                        .flatMap(permissionRequest -> buildPermissionEntity1(permissionRequest, roleEntity.getPermissionEntities()))
                        .collectList()
                        .map(permissionEntities -> {
                            permissionEntities.forEach(roleEntity::addPermissionEntity);
                            return roleEntity;
                        })
                );
    }

    public Mono<PermissionEntity> buildPermissionEntity1(PermissionRequest permissionRequest, List<PermissionEntity> permissionEntities) {
        return Flux.fromIterable(CommonUtil.returnListElseEmpty(permissionEntities)).filter(permissionEntity -> permissionEntity.fullyQualifiedFieldPath().equalsIgnoreCase(permissionEntity.fullyQualifiedFieldPath()))
                .next().switchIfEmpty(Mono.defer(() -> Mono.just(permissionRequestToEntityMapper.buildPermissionEntity(permissionRequest))));
    }
}
