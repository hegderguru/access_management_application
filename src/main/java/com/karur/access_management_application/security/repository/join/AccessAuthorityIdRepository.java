package com.karur.access_management_application.security.repository.join;

import com.karur.access_management_application.security.entity.join.AccessAuthorityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccessAuthorityIdRepository extends ReactiveCrudRepository<AccessAuthorityEntity,Long> {
    Flux<AccessAuthorityEntity> findByAccessId(Long accessId);
    Flux<AccessAuthorityEntity> findByAccessIdAndAuthorityId(Long accessId,Long authorityId);
}
