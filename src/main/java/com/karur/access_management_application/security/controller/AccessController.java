package com.karur.access_management_application.security.controller;

import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.response.AccessResponse;
import com.karur.access_management_application.security.service.AccessService;
import com.karur.access_management_application.validate.annotation.ValidateData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("access")
public class AccessController {

    @Autowired
    AccessService accessService;

    @GetMapping("accessDetail/{username}")
    public Mono<ResponseEntity<AccessResponse>> fetchAccessDetail(@PathVariable String username) {
        return accessService.fetchAccessDetails(username)
                .flatMap(accessDetail -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build())));
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<AccessResponse>> create(@RequestBody Mono<AccessRequest> accessRequestMono) {
        return accessRequestMono
                .flatMap(accessorRequest -> accessService.create(accessorRequest))
                .map(accessDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }

    @PutMapping("updateAccessDetail")
    public Mono<ResponseEntity<AccessResponse>> updateAccessDetail(@RequestBody Mono<AccessRequest> accessRequestMono) {
        return accessRequestMono
                .flatMap(accessorRequest -> accessService.update(accessorRequest))
                .map(accessDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }

}
