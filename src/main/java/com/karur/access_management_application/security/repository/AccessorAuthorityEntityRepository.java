package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.authentication.model.AuthorityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccessorAuthorityEntityRepository extends ReactiveCrudRepository<AuthorityEntity, Long> {
    Flux<AuthorityEntity> findByAccessorId(Long accessorId);
}
