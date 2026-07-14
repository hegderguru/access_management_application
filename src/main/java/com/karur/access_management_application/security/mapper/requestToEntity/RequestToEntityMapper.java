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
import com.karur.access_management_application.security.repository.inter.table.AccessEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.AuthorityEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.PermissionEntityRepository;
import com.karur.access_management_application.security.repository.inter.table.RoleEntityRepository;
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
        return Flux.fromIterable(authorityRequests)
                .flatMap(this::buildOnlyAuthorityEntity);
    }

    public Mono<AuthorityEntity> buildOnlyAuthorityEntity(AuthorityRequest authorityRequest) {
        return authorityEntityRepository.findByName(authorityRequest.getName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(authorityRequestToEntityMapper.buildAuthorityEntity(authorityRequest))));
    }

    public Flux<RoleEntity> buildOnlyRoleEntities(List<RoleRequest> roleRequests) {
        return Flux.fromIterable(roleRequests)
                .flatMap(this::buildOnlyRoleEntity);
    }

    public Mono<RoleEntity> buildOnlyRoleEntity(RoleRequest roleRequest) {
        return roleEntityRepository.findByName(roleRequest.getName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(roleRequestToEntityMapper.buildRoleEntity(roleRequest))));
    }

    public Flux<PermissionEntity> buildOnlyPermissionEntity(List<PermissionRequest> permissionRequests) {
        return Flux.fromIterable(permissionRequests)
                .flatMap(this::buildOnlyPermissionEntity);
    }

    public Mono<PermissionEntity> buildOnlyPermissionEntity(PermissionRequest permissionRequest) {
        return permissionEntityRepository.ClassPathAndClassNameAndFieldName(permissionRequest.getClassPath(), permissionRequest.getClassName(), permissionRequest.getFieldName())
                .switchIfEmpty(Mono.defer(() -> Mono.just(permissionRequestToEntityMapper.buildPermissionEntity(permissionRequest))));
    }

    public List<AccessAuthorityEntity> buildOnlyAccessAuthorityEntity(AccessEntity accessEntity, List<AuthorityEntity> authorityEntities) {
        return authorityEntities.stream().map(authorityEntity -> authorityRequestToEntityMapper.buildAccessAuthorityEntity(accessEntity.getId(), authorityEntity)).toList();
    }

    public List<AuthorityRoleEntity> buildOnlyAuthorityRoleEntity(AuthorityEntity authorityEntity, List<RoleEntity> roleEntities) {
        return roleEntities.stream().map(roleEntity -> roleRequestToEntityMapper.buildAuthorityRoleEntity(authorityEntity.getId(), roleEntity)).toList();
    }

    public List<RolePermissionEntity> buildOnlyRolePermissionEntity(RoleEntity roleEntity, List<PermissionEntity> permissionEntities) {
        return permissionEntities.stream().map(permissionEntity -> permissionRequestToEntityMapper.buildRolePermissionEntity(roleEntity.getId(), permissionEntity)).toList();
    }

}
