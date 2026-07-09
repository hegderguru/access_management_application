package com.karur.access_management_application.security.service;

import com.karur.access_management_application.security.repository.AccessorEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccessorDetailsService implements ReactiveUserDetailsService {

    @Autowired
    AccessorEntityRepository accessorEntityRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accessorEntityRepository.findById(username).flatMap(accessorEntity -> Mono.just((UserDetails) accessorEntity));
    }
}
