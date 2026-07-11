package com.karur.access_management_application.security.controller;

import com.karur.access_management_application.security.model.request.AccessorRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.model.request.PermissionRequest;
import com.karur.access_management_application.security.model.request.RoleRequest;
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

    @PostMapping("/welcome")
    public Mono<ResponseEntity<String>> welcome(){
        return Mono.just(ResponseEntity.ok("Welcome"));
    }

    @PostMapping("createAccess")
    public Mono<ResponseEntity<AccessResponse>> createAccessor(@RequestBody Mono<AccessorRequest> accessorRequestMono) {
        return accessorRequestMono.flatMap(accessorRequest -> accessorDetailsService.createAccessorEntity(accessorRequest))
                .flatMap(accessorEntity -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.CREATED).build())));
    }

    @PostMapping("createAuthority")
    public Mono<ResponseEntity<AccessResponse>> createAuthority(@RequestBody Mono<AuthorityRequest> authorityRequestMono) {
        return authorityRequestMono.flatMap(authorityRequest -> accessorDetailsService.createAuthority(authorityRequest))
                .flatMap(accessorEntity -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.CREATED).build())));
    }

    @PostMapping("createRole")
    public Mono<ResponseEntity<AccessResponse>> createRole(@RequestBody Mono<RoleRequest> roleRequestMono) {
        return roleRequestMono.flatMap(roleRequest -> accessorDetailsService.createRole(roleRequest))
                .flatMap(accessorEntity -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.CREATED).build())));
    }

    @PostMapping("createPermission")
    public Mono<ResponseEntity<AccessResponse>> createRole(@RequestBody Mono<PermissionRequest> permissionRequestMono) {
        return permissionRequestMono.flatMap(permissionRequest -> accessorDetailsService.createPermission(permissionRequest))
                .flatMap(accessorEntity -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.CREATED).build())));
    }

}
