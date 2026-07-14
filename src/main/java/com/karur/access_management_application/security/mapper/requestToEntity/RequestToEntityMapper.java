package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import com.karur.access_management_application.security.entity.join.AccessAuthorityEntity;
import com.karur.access_management_application.security.entity.join.AuthorityRoleEntity;
import com.karur.access_management_application.security.entity.join.RolePermissionEntity;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.repository.inter.joinTable.AccessAuthorityIdRepository;
import com.karur.access_management_application.security.repository.inter.joinTable.AuthorityRoleIdRepository;
import com.karur.access_management_application.security.repository.inter.joinTable.RolePermissionIdRepository;
import com.karur.access_management_application.security.repository.inter.table.AccessEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.AuthorityEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.PermissionEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.RoleEntityRepository;
import com.karur.access_management_application.security.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Mono<AccessEntity> buildOnlyAccessEntity(AccessRequest accessRequest) {
        return accessEntityRepository.findByUsername(accessRequest.getUsername())
                .switchIfEmpty(Mono.defer(() -> Mono.just(accessRequestToEntityMapper.buildAccessEntity(accessRequest))));
    }

    public Flux<AuthorityEntity> buildOnlyAuthorityEntities(List<AuthorityRequest> authorityRequests) {
        return Flux.fromIterable(CommonUtil.returnListElseEmpty(authorityRequests))
                .flatMap(this::buildOnlyAuthorityEntity);
    }

    public Mono<AuthorityEntity> buildOnlyAuthorityEntity(AuthorityRequest authorityRequest) {
        return authorityEntityRepository.findByName(authorityRequest.getName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(authorityRequestToEntityMapper.buildAuthorityEntity(authorityRequest))));
    }

    public Flux<RoleEntity> buildOnlyRoleEntities(List<RoleRequest> roleRequests) {
        return Flux.fromIterable(CommonUtil.returnListElseEmpty(roleRequests))
                .flatMap(this::buildOnlyRoleEntity);
    }

    public Mono<RoleEntity> buildOnlyRoleEntity(RoleRequest roleRequest) {
        return roleEntityRepository.findByName(roleRequest.getName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(roleRequestToEntityMapper.buildRoleEntity(roleRequest))));
    }

    public Flux<PermissionEntity> buildOnlyPermissionEntities(List<PermissionRequest> permissionRequests) {
        return Flux.fromIterable(CommonUtil.returnListElseEmpty(permissionRequests))
                .flatMap(this::buildOnlyPermissionEntity);
    }

    public Mono<PermissionEntity> buildOnlyPermissionEntity(PermissionRequest permissionRequest) {
        return permissionEntityRepository.ClassPathAndClassNameAndFieldName(permissionRequest.getClassPath(), permissionRequest.getClassName(), permissionRequest.getFieldName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(permissionRequestToEntityMapper.buildPermissionEntity(permissionRequest))));
    }

    public Flux<AccessAuthorityEntity> buildOnlyAccessAuthorityEntity(AccessEntity accessEntity, List<AuthorityEntity> authorityEntities) {
        return Flux.fromIterable(authorityEntities).flatMap(authorityEntity -> accessAuthorityIdRepository.findByAccessIdAndAuthorityId(accessEntity.getId(), authorityEntity.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.just(authorityRequestToEntityMapper.buildAccessAuthorityEntity(accessEntity.getId(), authorityEntity)))));
    }

    public Flux<AuthorityRoleEntity> buildOnlyAuthorityRoleEntity(AuthorityEntity authorityEntity, List<RoleEntity> roleEntities) {
        return Flux.fromIterable(roleEntities).flatMap(roleEntity -> authorityRoleIdRepository.findByAuthorityIdAndRoleId(authorityEntity.getId(), roleEntity.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.just(roleRequestToEntityMapper.buildAuthorityRoleEntity(authorityEntity.getId(), roleEntity))))
        );
    }

    public Flux<RolePermissionEntity> buildOnlyRolePermissionEntity(RoleEntity roleEntity, List<PermissionEntity> permissionEntities) {
        return Flux.fromIterable(permissionEntities).flatMap(permissionEntity -> rolePermissionIdRepository.findByRoleIdAndPermissionId(roleEntity.getId(), permissionEntity.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.just(permissionRequestToEntityMapper.buildRolePermissionEntity(roleEntity.getId(), permissionEntity)))));
    }

    public Mono<AccessEntity> buildAndMapOnlyAccessEntity(AccessRequest accessRequest) {
        return buildOnlyAccessEntity(accessRequest)
                .flatMap(accessEntity -> buildOnlyAuthorityEntities(CommonUtil.returnListElseEmpty(accessRequest.getAuthorityRequests()))
                        .collectList()
                        .flatMap(authorityEntities -> {
                            accessEntity.setAuthorityEntities(authorityEntities);
                            return buildOnlyAccessAuthorityEntity(accessEntity, authorityEntities)
                                    .then(Mono.just(accessEntity));
                        })
                );
    }

    public Flux<AuthorityEntity> buildAndMapOnlyAuthorityEntities(List<AuthorityRequest> authorityRequests) {
        return Flux.fromIterable(authorityRequests)
                .flatMap(this::buildAndMapOnlyAuthorityEntity);
    }

    public Mono<AuthorityEntity> buildAndMapOnlyAuthorityEntity(AuthorityRequest authorityRequest) {
        return buildOnlyAuthorityEntity(authorityRequest)
                .flatMap(authorityEntity -> buildOnlyRoleEntities(CommonUtil.returnListElseEmpty(authorityRequest.getRoleRequests()))
                        .collectList()
                        .flatMap(roleEntities -> {
                            authorityEntity.setRoleEntities(roleEntities);
                            return buildOnlyAuthorityRoleEntity(authorityEntity, roleEntities)
                                    .then(Mono.just(authorityEntity));
                        }));
    }

    public Flux<RoleEntity> buildAndMapOnlyRoleEntities(List<RoleRequest> roleRequests) {
        return Flux.fromIterable(roleRequests).flatMap(this::buildAndMapOnlyRoleEntity);
    }

    public Mono<RoleEntity> buildAndMapOnlyRoleEntity(RoleRequest roleRequest) {
        return buildOnlyRoleEntity(roleRequest)
                .flatMap(roleEntity -> buildOnlyPermissionEntities(CommonUtil.returnListElseEmpty(roleRequest.getPermissionRequests()))
                        .collectList()
                        .flatMap(permissionEntities -> {
                            roleEntity.setPermissionEntities(permissionEntities);
                            return buildOnlyRolePermissionEntity(roleEntity, permissionEntities)
                                    .then(Mono.just(roleEntity));
                        }));
    }

    public Mono<AccessEntity> buildAndMapAccessEntity(AccessRequest accessRequest) {
        return buildAndMapOnlyAccessEntity(accessRequest)
                .flatMap(accessEntity -> buildAndMapAuthorityEntities(CommonUtil.returnListElseEmpty(accessRequest.getAuthorityRequests()))
                        .then(Mono.defer(() -> Mono.just(accessEntity))));
    }

    public Flux<AuthorityEntity> buildAndMapAuthorityEntities(List<AuthorityRequest> authorityRequests) {
        return Flux.fromIterable(authorityRequests).flatMap(this::buildAndMapAuthorityEntity);
    }

    public Mono<AuthorityEntity> buildAndMapAuthorityEntity(AuthorityRequest authorityRequest) {
        return buildAndMapOnlyAuthorityEntity(authorityRequest)
                .flatMap(authorityEntity -> buildAndMapOnlyRoleEntities(CommonUtil.returnListElseEmpty(authorityRequest.getRoleRequests()))
                        .then(Mono.defer(() -> Mono.just(authorityEntity))));
    }
}
