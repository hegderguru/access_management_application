package com.karur.access_management_application.security.controller;

import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
import com.karur.access_management_application.security.model.response.AccessResponse;
import com.karur.access_management_application.security.service.AccessDetailsService;
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
    AccessDetailsService accessDetailsService;

    @PostMapping("/welcome")
    public Mono<ResponseEntity<String>> welcome(){
        return Mono.just(ResponseEntity.ok("Welcome"));
    }

    @PostMapping("createAccess")
    public Mono<ResponseEntity<AccessResponse>> createAccessor(@RequestBody Mono<AccessRequest> accessorRequestMono) {
        return accessorRequestMono.flatMap(accessorRequest -> accessDetailsService.createAccessEntity(accessorRequest))
                .flatMap(accessorEntity -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.CREATED).build())));
    }

    @PostMapping("createAuthority")
    public Mono<ResponseEntity<AccessResponse>> createAuthority(@RequestBody Mono<AuthorityRequest> authorityRequestMono) {
        return authorityRequestMono.flatMap(authorityRequest -> accessDetailsService.createAuthority(authorityRequest))
                .flatMap(accessorEntity -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.CREATED).build())));
    }

    @PostMapping("createRole")
    public Mono<ResponseEntity<AccessResponse>> createRole(@RequestBody Mono<RoleRequest> roleRequestMono) {
        return roleRequestMono.flatMap(roleRequest -> accessDetailsService.createRole(roleRequest))
                .flatMap(accessorEntity -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.CREATED).build())));
    }

    @PostMapping("createPermission")
    public Mono<ResponseEntity<AccessResponse>> createPermission(@RequestBody Mono<PermissionRequest> permissionRequestMono) {
        return permissionRequestMono.flatMap(permissionRequest -> accessDetailsService.createPermission(permissionRequest))
                .flatMap(accessorEntity -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.CREATED).build())));
    }

}
