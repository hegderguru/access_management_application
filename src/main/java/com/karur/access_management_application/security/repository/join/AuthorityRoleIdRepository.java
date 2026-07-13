package com.karur.access_management_application.security.repository.join;

import com.karur.access_management_application.security.entity.join.AuthorityRoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AuthorityRoleIdRepository extends ReactiveCrudRepository<AuthorityRoleEntity, Long> {
    Flux<AuthorityRoleEntity> findByAuthorityId(Long authorityId);
    Flux<AuthorityRoleEntity> findByAuthorityIdAndRoleId(Long authorityId,Long roleId);
}
