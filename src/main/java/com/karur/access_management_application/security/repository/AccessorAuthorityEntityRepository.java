package com.karur.access_management_application.security.repository;

import com.karur.access_management_application.security.authentication.model.AccessGrantedAuthority;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccessorAuthorityEntityRepository extends ReactiveCrudRepository<AccessGrantedAuthority, Long> {
    Flux<AccessGrantedAuthority> findByAccessorId(Long accessorId);
}
