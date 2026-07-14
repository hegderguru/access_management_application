package com.karur.access_management_application.security.controller;

import com.karur.access_management_application.security.model.request.AccessRequest;
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
    AccessService accessService;

    @PostMapping("accessDetail/{username}")
    public Mono<ResponseEntity<AccessResponse>> fetchAccessDetail(@PathVariable String username) {
        return accessService.fetchAccessDetails(username)
                .flatMap(accessDetail -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build())));
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<AccessResponse>> create(@RequestBody Mono<AccessRequest> accessRequestMono) {
        return updateAccessDetail(accessRequestMono);
    }

    @PutMapping("updateAccessDetail")
    public Mono<ResponseEntity<AccessResponse>> updateAccessDetail(@RequestBody Mono<AccessRequest> accessRequestMono) {
        return accessRequestMono.flatMap(accessorRequest -> accessService.update(accessorRequest))
                .flatMap(accessDetail -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build())));
    }

}
