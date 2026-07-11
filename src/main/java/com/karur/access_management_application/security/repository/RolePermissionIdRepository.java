package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.AccessAuthorityEntity;
import com.karur.access_management_application.security.entity.AuthorityRoleEntity;
import com.karur.access_management_application.security.entity.RolePermissionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RolePermissionIdRepository extends ReactiveCrudRepository<RolePermissionEntity, Long> {
    Flux<RolePermissionEntity> findByRoleId(Long roleId);

    Flux<RolePermissionEntity> findByPermissionId(Long permissionId);
}
