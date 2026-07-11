package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.mapper.requestToEntity.RequestToEntityMapper;
import com.karur.access_management_application.security.model.request.AccessorRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.repository.AccessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccessorDetailsService implements ReactiveUserDetailsService {

    @Autowired
    AccessorRepository accessorRepository;

    @Autowired
    RequestToEntityMapper requestToEntityMapper;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accessorRepository.findAccessorEntityByUsername(username).flatMap(accessorEntity -> Mono.just((UserDetails) accessorEntity));
    }

    public Mono<AccessEntity> createAccessorEntity(AccessorRequest accessorRequest) {
        AccessEntity accessEntity = requestToEntityMapper.buildAccessorEntity(accessorRequest);
        return accessorRepository.save(accessEntity);
    }

    public Mono<AuthorityEntity> createAuthority(AuthorityRequest authorityRequest) {
        AuthorityEntity authorityEntity = requestToEntityMapper.buildAccessGrantedAuthorityEntity(authorityRequest);
        return accessorRepository.save(authorityEntity);
    }

}
