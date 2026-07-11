package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class AccessRepository {

    @Autowired
    AccessEntityRepository accessEntityRepository;

    @Autowired
    AuthorityEntityRepository authorityEntityRepository;

    @Autowired
    RoleEntityRepository roleEntityRepository;

    @Autowired
    PermissionEntityRepository permissionEntityRepository;

    @Autowired
    RolePermissionIdRepository rolePermissionIdRepository;

    @Autowired
    AuthorityRoleIdRepository authorityRoleIdRepository;

    @Autowired
    AccessAuthorityIdRepository accessAuthorityIdRepository;

    public Mono<AccessEntity> findAccessorEntityByUsername(String username) {
        return accessEntityRepository.findByUsername(username)
                .flatMap(accessorEntity ->
                        authorityEntityRepository.findByAccessorId(accessorEntity.getId())
                                .collectList()
                                .flatMap(accessGrantedAuthorities -> {
                                    accessorEntity.setAccessGrantedAuthorities(accessGrantedAuthorities);
                                    // Map over each authority to fetch its roles reactively
                                    return Flux.fromIterable(accessGrantedAuthorities)
                                            .flatMap(accessGrantedAuthority ->
                                                    roleEntityRepository.findByAuthorityId(accessGrantedAuthority.getId())
                                                            .collectList()
                                                            .flatMap(accessRoleEntities -> {
                                                                accessGrantedAuthority.setAccessRoleEntities(accessRoleEntities); // Note: renamed setter to match standard naming convention
                                                                // Map over each role to fetch its permissions reactively
                                                                return Flux.fromIterable(accessRoleEntities)
                                                                        .flatMap(accessRoleEntity ->
                                                                                permissionEntityRepository.findByRoleId(accessRoleEntity.getId())
                                                                                        .collectList()
                                                                                        .doOnNext(accessRoleEntity::setAccessPermissionEntities
                                                                                        )
                                                                        )
                                                                        .then(Mono.just(accessGrantedAuthority)); // Return authority after inner processing finishes
                                                            })
                                            )
                                            .then(Mono.just(accessorEntity)); // Return the complete root entity after everything is assembled
                                })
                );
    }

    public Flux<AccessAuthorityEntity> findAllAccessAuthorityEntities(AccessEntity accessEntity){
        return accessAuthorityIdRepository.findByAccessId(accessEntity.getId());
    }

    public Flux<AuthorityRoleEntity> findAllAuthorityRoleEntities(AuthorityEntity authorityEntity){
        return authorityRoleIdRepository.findByAuthorityId(authorityEntity.getId());
    }

    public Flux<RolePermissionEntity> findAllRolePermissionEntities(RoleEntity roleEntity){
        return rolePermissionIdRepository.findByRoleId(roleEntity.getId());
    }

    public Flux<AuthorityEntity> fetchAllAuthorityEntities(List<AccessAuthorityEntity> accessAuthorityEntities){
        List<Long> ids = accessAuthorityEntities.stream().map(AccessAuthorityEntity::authorityId).toList();
        return fetchAuthorityEntities(ids);
    }

    public Flux<AuthorityEntity> fetchAuthorityEntities(List<Long> ids){
        return authorityEntityRepository.findByIdIn(ids);
    }

    public Mono<AuthorityEntity> fetchAuthorityEntity(Long id){
        return authorityEntityRepository.findById(id);
    }

    public Flux<RoleEntity> fetchAllRoleEntities(List<AuthorityRoleEntity> authorityRoleEntities){
        List<Long> ids = authorityRoleEntities.stream().map(AuthorityRoleEntity::roleId).toList();
        return fetchRoleEntities(ids);
    }

    public Flux<RoleEntity> fetchRoleEntities(List<Long> ids){
        return roleEntityRepository.findByIdIn(ids);
    }

    public Mono<RoleEntity> fetchRoleEntity(Long id){
        return roleEntityRepository.findById(id);
    }

    public Flux<PermissionEntity> fetchAllPermissionEntities(List<RolePermissionEntity> rolePermissionEntities){
        List<Long> ids = rolePermissionEntities.stream().map(RolePermissionEntity::permissionId).toList();
        return fetchPermissionEntities(ids);
    }

    public Flux<PermissionEntity> fetchPermissionEntities(List<Long> ids){
        return permissionEntityRepository.findByIdIn(ids);
    }

    public Mono<PermissionEntity> fetchPermissionEntity(Long id){
        return permissionEntityRepository.findById(id);
    }

    public Mono<AccessEntity> save(AccessEntity accessEntity) {
        return accessEntityRepository.save(accessEntity)
                .flatMap(accessorEntity1 -> {
                    accessorEntity1.accessGrantedAuthorities().forEach(accessGrantedAuthorityEntity -> accessGrantedAuthorityEntity.setAccessorId(accessorEntity1.getId()));
                    return authorityEntityRepository.saveAll(accessEntity.accessGrantedAuthorities()).then(Mono.just(accessorEntity1));
                });
    }

    public Mono<AuthorityEntity> save(AuthorityEntity authorityEntity) {
        return authorityEntityRepository.save(authorityEntity)
                .flatMap(accessGrantedAuthorityEntity1 -> {
                    accessGrantedAuthorityEntity1.getAccessRoleEntities().forEach(accessRoleEntity -> accessRoleEntity.setAuthorityId(accessGrantedAuthorityEntity1.getId()));
                    return roleEntityRepository.saveAll(accessGrantedAuthorityEntity1.getAccessRoleEntities()).then(Mono.just(accessGrantedAuthorityEntity1));
                });
    }

    public Mono<RoleEntity> save(RoleEntity roleEntity) {
        return roleEntityRepository.save(roleEntity)
                .flatMap(roleEntity1 -> {
                    roleEntity1.getAccessPermissionEntities().forEach(permissionEntity -> permissionEntity.setRoleId(roleEntity1.getId()));
                    return permissionEntityRepository.saveAll(roleEntity1.getAccessPermissionEntities()).then(Mono.just(roleEntity1));
                });
    }

    public Mono<PermissionEntity> save(PermissionEntity permissionEntity) {
        return permissionEntityRepository.save(permissionEntity);
    }
}
