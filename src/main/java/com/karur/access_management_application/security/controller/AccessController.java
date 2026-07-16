package com.karur.access_management_application.security.controller;

import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.model.response.AccessResponse;
import com.karur.access_management_application.security.service.AccessService;
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

    @PostMapping("/createAccess")
    public Mono<ResponseEntity<AccessResponse>> createAccess(@RequestBody Mono<AccessRequest> accessRequestMono) {
        return accessRequestMono
                .flatMap(accessorRequest -> accessService.createAccess(accessorRequest))
                .map(accessDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }

    @PostMapping("/createAuthority")
    public Mono<ResponseEntity<AccessResponse>> createAuthority(@RequestBody Mono<AuthorityRequest> authorityRequestMono) {
        return authorityRequestMono
                .flatMap(authorityRequest -> accessService.createAuthority(authorityRequest))
                .map(authorityDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).authorityDetail(authorityDetail).build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }

    @PostMapping("/createRole")
    public Mono<ResponseEntity<AccessResponse>> createRole(@RequestBody Mono<RoleRequest> roleRequestMono) {
        return roleRequestMono
                .flatMap(roleRequest -> accessService.createRole(roleRequest))
                .map(roleDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).roleDetail(roleDetail).build()
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
