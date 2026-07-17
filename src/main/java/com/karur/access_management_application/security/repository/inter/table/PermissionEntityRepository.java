package com.karur.access_management_application.security.repository.inter.table;

import com.karur.access_management_application.security.entity.PermissionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface PermissionEntityRepository extends ReactiveCrudRepository<PermissionEntity,Long> {
    Flux<PermissionEntity> findByIdIn(List<Long> ids);

    Mono<PermissionEntity> findByAppIdAndFullyQualifiedFieldNameAndReadAndCreateAndUpdateAndDelete(String appId,String fullyQualifiedFieldName,Boolean read_,Boolean create_,Boolean update_,Boolean delete_);
}
