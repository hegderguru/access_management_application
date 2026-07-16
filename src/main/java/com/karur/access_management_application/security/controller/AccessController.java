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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("access")
public class AccessController {

    @Autowired
    AccessService accessService;

    /*Read Starts*/
    @GetMapping("accessDetail/{username}")
    public Mono<ResponseEntity<AccessResponse>> fetchAccessDetail(@PathVariable String username) {
        return accessService.fetchAccessDetails(username)
                .flatMap(accessDetail -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build())));
    }

    @GetMapping("authorityDetail/{name}")
    public Mono<ResponseEntity<AccessResponse>> fetchAuthorityDetail(@RequestHeader("X-REQUESTER-NAME") String requesterName, @PathVariable String name) {
        return accessService.fetchAuthorityDetail(requesterName + "-" + name)
                .map(authorityDetail -> ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.OK).authorityDetail(authorityDetail).build()))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }

    @GetMapping("roleDetail/{name}")
    public Mono<ResponseEntity<AccessResponse>> fetchRoleDetail(@RequestHeader("X-REQUESTER-NAME") String requesterName, @PathVariable String name) {
        return accessService.fetchRoleDetail(requesterName + "-" + name)
                .flatMap(roleDetail -> Mono.just(ResponseEntity.ok(AccessResponse.builder().httpStatus(HttpStatus.OK).roleDetail(roleDetail).build())));
    }
    /*Read Ends*/

    /*Create Starts*/
    @PostMapping("/createAccess")
    public Mono<ResponseEntity<AccessResponse>> createAccess(@RequestHeader("X-REQUESTER-NAME") String requesterName, @RequestBody Mono<AccessRequest> accessRequestMono) {
        return accessRequestMono
                .flatMap(accessorRequest -> {
                    accessorRequest.setUsername(requesterName + "-" + accessorRequest.getUsername());
                    return accessService.createAccess(accessorRequest);
                })
                .map(accessDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }

    @PostMapping("/createAuthority")
    public Mono<ResponseEntity<AccessResponse>> createAuthority(@RequestHeader("X-REQUESTER-NAME") String requesterName, @RequestBody Mono<AuthorityRequest> authorityRequestMono) {
        return authorityRequestMono
                .flatMap(authorityRequest -> {
                    authorityRequest.setName(requesterName + "-" + authorityRequest.getName());
                    return accessService.createAuthority(authorityRequest);
                })
                .map(authorityDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).authorityDetail(authorityDetail).build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }

    @PostMapping("/createRole")
    public Mono<ResponseEntity<AccessResponse>> createRole(@RequestHeader("X-REQUESTER-NAME") String requesterName, @RequestBody Mono<RoleRequest> roleRequestMono) {
        return roleRequestMono
                .flatMap(roleRequest -> {
                    roleRequest.setName(requesterName + "-" + roleRequest.getName());
                    return accessService.createRole(roleRequest);
                })
                .map(roleDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).roleDetail(roleDetail).build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }
    /*Create End*/

    /*Update starts*/
    @PutMapping("/updateAccess")
    public Mono<ResponseEntity<AccessResponse>> updateAccess(@RequestHeader("X-REQUESTER-NAME") String requesterName, @RequestBody Mono<AccessRequest> accessRequestMono) {
        return accessRequestMono
                .flatMap(accessorRequest -> {
                    accessorRequest.setUsername(requesterName + "-" + accessorRequest.getUsername());
                    return accessService.updateAccess(accessorRequest);
                })
                .map(accessDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).accessDetail(accessDetail).build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }

    @PutMapping("/updateAuthority")
    public Mono<ResponseEntity<AccessResponse>> updateAuthority(@RequestHeader("X-REQUESTER-NAME") String requesterName, @RequestBody Mono<AuthorityRequest> authorityRequestMono) {
        return authorityRequestMono
                .flatMap(authorityRequest -> {
                    authorityRequest.setName(requesterName + "-" + authorityRequest.getName());
                    return accessService.updateAuthority(authorityRequest);
                })
                .map(authorityDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).authorityDetail(authorityDetail).build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }

    @PutMapping("/updateRole")
    public Mono<ResponseEntity<AccessResponse>> updateRole(@RequestHeader("X-REQUESTER-NAME") String requesterName, @RequestBody Mono<RoleRequest> roleRequestMono) {
        return roleRequestMono
                .flatMap(roleRequest -> {
                    roleRequest.setName(requesterName + "-" + roleRequest.getName());
                    return accessService.updateRole(roleRequest);
                })
                .map(roleDetail -> ResponseEntity.ok(
                        AccessResponse.builder().httpStatus(HttpStatus.OK).roleDetail(roleDetail).build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Failed to update access details: ", throwable);
                    return Mono.just(ResponseEntity.badRequest().body(AccessResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build()));
                });
    }
    /*Update ends*/

    @GetMapping("/permissions")
    public Mono<Void> createPermissions(@RequestBody List<Boolean[]> permissions) {
        return accessService.createPermissions(permissions).then();
    }

}
