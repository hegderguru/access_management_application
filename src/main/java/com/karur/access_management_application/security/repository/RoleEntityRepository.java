package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.entity.PermissionEntity;
import com.karur.access_management_application.security.entity.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface RoleEntityRepository extends ReactiveCrudRepository<RoleEntity,Long> {
    Flux<RoleEntity> findByIdIn(List<Long> ids);
}
