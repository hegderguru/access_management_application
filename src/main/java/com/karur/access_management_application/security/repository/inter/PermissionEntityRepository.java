package com.karur.access_management_application.security.repository.inter;

import com.karur.access_management_application.security.entity.PermissionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface PermissionEntityRepository extends ReactiveCrudRepository<PermissionEntity,Long> {
    Flux<PermissionEntity> findByIdIn(List<Long> ids);
}
