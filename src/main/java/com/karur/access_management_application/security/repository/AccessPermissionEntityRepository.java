package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.authentication.model.AccessPermissionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccessPermissionEntityRepository extends ReactiveCrudRepository<AccessPermissionEntity,Long> {
    Flux<AccessPermissionEntity> findByRoleId(Long roleId);
}
