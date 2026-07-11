package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.PermissionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PermissionEntityRepository extends ReactiveCrudRepository<PermissionEntity,Long> {
    Flux<PermissionEntity> findByRoleId(Long roleId);
}
