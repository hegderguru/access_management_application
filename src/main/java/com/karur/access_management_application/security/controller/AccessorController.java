package com.karur.access_management_application.security.controller;

import com.karur.access_management_application.security.model.request.AccessorRequest;
import com.karur.access_management_application.security.model.response.AccessResponse;
import com.karur.access_management_application.security.service.AccessorDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("accessor")
public class AccessorController {

    @Autowired
    AccessorDetailsService accessorDetailsService;

    @PostMapping("create")
    public Mono<ResponseEntity<AccessResponse>> create(@RequestBody AccessorRequest accessorRequest) {
        return accessorDetailsService.createAccessorEntity(accessorRequest)
                .flatMap(accessorEntity->Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.CREATED).build())));
    }


}
