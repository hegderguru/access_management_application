package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.compare.ChangeUtil;
import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.*;
import com.karur.access_management_application.security.model.request.AccessRequest;
import com.karur.access_management_application.security.util.AccessRequestUpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccessRequestToEntityMapper {

    @Autowired
    PasswordEncoder passwordEncoder;

    public AccessEntity buildAccessEntity(AccessRequest accessRequest) {
        return AccessEntity.builder()
                .username(accessRequest.getUsername())
                .password(passwordEncoder.encode(accessRequest.getPassword()))
                .firstName(accessRequest.getFirstName())
                .middleName(accessRequest.getMiddleName())
                .lastName(accessRequest.getLastName())
                .accessEnabled(true)
                .accessExpired(false)
                .credentialsExpired(false)
                .accessLocked(false)
                .authorityEntities(new ArrayList<>())
                .build();
    }

    public Mono<Void> updateAccessOnChanges(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessRequestUpdateUtil.getUpdateAccessRequest(changes))
                .flatMap(change -> {
                    switch (AccessRequest.Fields.valueOf(change.getField())) {
                        case firstName -> accessEntity.setFirstName(ChangeUtil.getStringElseConvert(change));
                        case middleName -> accessEntity.setMiddleName(ChangeUtil.getStringElseConvert(change));
                        case lastName -> accessEntity.setLastName(ChangeUtil.getStringElseConvert(change));
                        case accessEnabled -> accessEntity.setAccessEnabled(ChangeUtil.getBooleanElseConvert(change));
                        case accessLocked -> accessEntity.setAccessLocked(ChangeUtil.getBooleanElseConvert(change));
                        case accessExpired -> accessEntity.setAccessExpired(ChangeUtil.getBooleanElseConvert(change));
                        case credentialsExpired ->
                                accessEntity.setCredentialsExpired(ChangeUtil.getBooleanElseConvert(change));
                    }
                    return Mono.empty();
                }).then();
    }

}
