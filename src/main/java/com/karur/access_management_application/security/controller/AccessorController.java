package com.karur.access_management_application.security.controller;

import com.karur.access_management_application.security.model.response.AccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("accessor")
public class AccessorController {

    @PostMapping("create")
    public Mono<ResponseEntity<AccessResponse>> create(){
        return null;
    }


}
