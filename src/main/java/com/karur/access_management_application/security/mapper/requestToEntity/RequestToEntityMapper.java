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

    ///


    //Access Mapping
    public Mono<AccessEntity> buildAndMapAccessEntity(AccessRequest accessRequest) {
        return buildAndMapOnlyAccessEntity(accessRequest)
                .flatMap(accessEntity -> Flux.fromIterable(CommonUtil.returnListElseEmpty(accessRequest.getAuthorityRequests()))
                        .flatMap(this::buildAndMapAuthorityEntity)
                        .collectList()
                        .doOnSuccess(authorityEntities -> {
                            log.info("authorityEntities: {}", authorityEntities);
                        })
                        .thenReturn(accessEntity));
    }
    //Access Mapping

    // Authority Mapping
    public Mono<AuthorityEntity> buildAndMapAuthorityEntity(AuthorityRequest authorityRequest) {
        return buildAndMapOnlyAuthorityEntity(authorityRequest)
                .flatMap(authorityEntity -> Flux.fromIterable(CommonUtil.returnListElseEmpty(authorityRequest.getRoleRequests()))
                        .flatMap(this::buildAndMapOnlyRoleEntity)
                        .then(Mono.defer(() -> Mono.just(authorityEntity))));
    }
    // Authority Mapping

    //Mappings start
    public Mono<AccessEntity> buildAndMapOnlyAccessEntity(AccessRequest accessRequest) {
        return buildOnlyAccessEntity(accessRequest)
                .flatMap(accessEntity -> buildOnlyAuthorityEntities(CommonUtil.returnListElseEmpty(accessRequest.getAuthorityRequests()))
                        .collectList()
                        .flatMap(authorityEntities -> {
                            accessEntity.setAuthorityEntities(authorityEntities);
                            return accessRepository.buildOnlyAccessAuthorityEntity(accessEntity, authorityEntities)
                                    .then(Mono.just(accessEntity));
                        })
                );
    }

    public Mono<AuthorityEntity> buildAndMapOnlyAuthorityEntity(AuthorityRequest authorityRequest) {
        return buildOnlyAuthorityEntity(authorityRequest)
                .flatMap(authorityEntity -> buildOnlyRoleEntities(CommonUtil.returnListElseEmpty(authorityRequest.getRoleRequests()))
                        .collectList()
                        .flatMap(roleEntities -> {
                            authorityEntity.setRoleEntities(roleEntities);
                            return accessRepository.buildOnlyAuthorityRoleEntity(authorityEntity, roleEntities)
                                    .then(Mono.just(authorityEntity));
                        }));
    }

    public Mono<RoleEntity> buildAndMapOnlyRoleEntity(RoleRequest roleRequest) {
        return buildOnlyRoleEntity(roleRequest)
                .flatMap(roleEntity -> buildOnlyPermissionEntities(CommonUtil.returnListElseEmpty(roleRequest.getPermissionRequests()))
                        .collectList()
                        .flatMap(permissionEntities -> {
                            roleEntity.setPermissionEntities(permissionEntities);
                            return accessRepository.buildOnlyRolePermissionEntity(roleEntity, permissionEntities)
                                    .then(Mono.just(roleEntity));
                        }));
    }
    //Mapping Ends

    //Multiple Entity starts
    public Flux<AuthorityEntity> buildOnlyAuthorityEntities(List<AuthorityRequest> authorityRequests) {
        return Flux.fromIterable(CommonUtil.returnListElseEmpty(authorityRequests))
                .flatMap(this::buildOnlyAuthorityEntity);
    }

    public Flux<RoleEntity> buildOnlyRoleEntities(List<RoleRequest> roleRequests) {
        return Flux.fromIterable(CommonUtil.returnListElseEmpty(roleRequests))
                .flatMap(this::buildOnlyRoleEntity);
    }

    public Flux<PermissionEntity> buildOnlyPermissionEntities(List<PermissionRequest> permissionRequests) {
        return Flux.fromIterable(CommonUtil.returnListElseEmpty(permissionRequests))
                .flatMap(this::buildOnlyPermissionEntity);
    }
    //Multiple Entity starts

    //Single Entity starts
    public Mono<AccessEntity> buildOnlyAccessEntity(AccessRequest accessRequest) {
        return accessEntityRepository.findByUsername(accessRequest.getUsername())
                .switchIfEmpty(Mono.defer(() -> Mono.just(accessRequestToEntityMapper.buildAccessEntity(accessRequest))));
    }

    public Mono<AuthorityEntity> buildOnlyAuthorityEntity(AuthorityRequest authorityRequest) {
        return authorityEntityRepository.findByName(authorityRequest.getName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(authorityRequestToEntityMapper.buildAuthorityEntity(authorityRequest))));
    }

    public Mono<RoleEntity> buildOnlyRoleEntity(RoleRequest roleRequest) {
        return roleEntityRepository.findByName(roleRequest.getName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(roleRequestToEntityMapper.buildRoleEntity(roleRequest))));
    }

    public Mono<PermissionEntity> buildOnlyPermissionEntity(PermissionRequest permissionRequest) {
        return permissionEntityRepository.ClassPathAndClassNameAndFieldName(permissionRequest.getClassPath(), permissionRequest.getClassName(), permissionRequest.getFieldName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(permissionRequestToEntityMapper.buildPermissionEntity(permissionRequest))));
    }
    //Single Entity ends

    /*New Implementation*/

    public Mono<AccessEntity> buildAccessEntity1(AccessRequest accessRequest) {
        return accessEntityRepository.findByUsername(accessRequest.getUsername())
                .switchIfEmpty(Mono.defer(() -> Mono.just(accessRequestToEntityMapper.buildAccessEntity(accessRequest))))
                .flatMap(accessEntity -> Flux.fromIterable(CommonUtil.returnListElseEmpty(accessRequest.getAuthorityRequests()))
                        .flatMap(this::buildAuthorityEntity1)
                        .collectList()
                        .map(authorityEntities -> {
                            authorityEntities.forEach(accessEntity::addAuthorityEntity);
                            return accessEntity;
                        })
                );
    }

    public Mono<AuthorityEntity> buildAuthorityEntity1(AuthorityRequest authorityRequest) {
        return authorityEntityRepository.findByName(authorityRequest.getName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(authorityRequestToEntityMapper.buildAuthorityEntity(authorityRequest))))
                .flatMap(authorityEntity -> Flux.fromIterable(CommonUtil.returnListElseEmpty(authorityRequest.getRoleRequests()))
                        .flatMap(this::buildRoleEntity1)
                        .collectList()
                        .map(roleEntities -> {
                            roleEntities.forEach(authorityEntity::addRoleEntity);
                            return authorityEntity;
                        })
                );
    }

    public Mono<RoleEntity> buildRoleEntity1(RoleRequest roleRequest) {
        return roleEntityRepository.findByName(roleRequest.getName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(roleRequestToEntityMapper.buildRoleEntity(roleRequest))))
                .flatMap(roleEntity -> Flux.fromIterable(CommonUtil.returnListElseEmpty(roleRequest.getPermissionRequests()))
                        .flatMap(this::buildPermissionEntity1)
                        .collectList()
                        .map(permissionEntities -> {
                            permissionEntities.forEach(roleEntity::addPermissionEntity);
                            return roleEntity;
                        })
                );
    }

    public Mono<PermissionEntity> buildPermissionEntity1(PermissionRequest permissionRequest) {
        return permissionEntityRepository.ClassPathAndClassNameAndFieldName(permissionRequest.getClassPath(), permissionRequest.getClassName(), permissionRequest.getFieldName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(permissionRequestToEntityMapper.buildPermissionEntity(permissionRequest))));
    }
}
