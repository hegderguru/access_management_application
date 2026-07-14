package com.karur.access_management_application.security.controller;

import com.karur.access_management_application.security.mapper.entityToRead.EntityToReadMapper;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.model.response.AccessResponse;
import com.karur.access_management_application.security.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("access")
public class AccessController {

    @Autowired
    EntityToReadMapper entityToReadMapper;

    @Autowired
    AccessService accessService;

    @PostMapping("/create")
    public Mono<ResponseEntity<AccessResponse>> create(@RequestBody Mono<AccessRequest> accessRequestMono) {
        return updateAccessDetail(accessRequestMono);
    }

    @PostMapping("accessDetail")
    public Mono<ResponseEntity<AccessResponse>> fetchAccessDetail(@RequestBody Mono<AccessRequest> accessRequestMono) {
        return accessRequestMono.flatMap(accessorRequest -> accessService.fetchAccessDetails(accessorRequest.getUsername()))
                .flatMap(accessDetail -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build())));
    }

    @PostMapping("authorityDetails")
    public Mono<ResponseEntity<AccessResponse>> fetchAuthorityDetails(@RequestBody Mono<AccessRequest> accessRequestMono) {
        return accessRequestMono.flatMap(accessorRequest -> accessService.fetchAuthorityDetails(accessorRequest.getUsername()))
                .flatMap(accessDetail -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build())));
    }

    @PutMapping("updateAccessDetail")
    public Mono<ResponseEntity<AccessResponse>> updateAccessDetail(@RequestBody Mono<AccessRequest> accessRequestMono) {
        return accessRequestMono.flatMap(accessorRequest -> accessService.update(accessorRequest))
                .flatMap(accessDetail -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build())));
    }

}
