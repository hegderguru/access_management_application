package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.compare.ChangeUtil;
import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.join.AccessAuthorityEntity;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.util.AccessRequestUpdateUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AuthorityRequestToEntityMapper {

    public AccessAuthorityEntity buildAccessAuthorityEntity(Long accessId, AuthorityEntity authorityEntity) {
        return AccessAuthorityEntity.builder()
                .accessId(accessId)
                .authorityId(authorityEntity.getId())
                .build();
    }

    public AuthorityEntity buildAuthorityEntity(AuthorityRequest authorityRequest) {
        return AuthorityEntity.builder()
                .roleEntities(new ArrayList<>())
                .name(authorityRequest.getName())
                .description(authorityRequest.getDescription())
                .build();
    }

    public Mono<Void> saveOrUpdateAuthoritiesOnChanges(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return newAuthoritiesOnChanges(accessEntity, changes).then(Mono.defer(() -> updateAuthoritiesOnChanges(accessEntity, changes)));
    }

    public Mono<Void> newAuthoritiesOnChanges(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(AccessRequestUpdateUtil.getNewAuthorityRequest(changes))
                .flatMap(change -> Mono.just(buildAuthorityEntity((AuthorityRequest) change.getRightValue())))
                .flatMap(authorityEntity -> {
                    accessEntity.addAuthorityEntity(authorityEntity);
                    return Mono.empty();
                }).then();
    }

    public Mono<Void> updateAuthoritiesOnChanges(AccessEntity accessEntity, List<CompareUtil.Change> changes) {
        Map<String, List<CompareUtil.Change>> updateAuthorityRequest1 = AccessRequestUpdateUtil.getUpdateAuthorityRequest(changes);
        return Flux.fromIterable(updateAuthorityRequest1.entrySet())
                .flatMap(stringListEntry -> {
                    AuthorityEntity authorityEntity = accessEntity.getAuthorityEntities().stream().filter(authorityEntity1 -> authorityEntity1.getName().equalsIgnoreCase(stringListEntry.getKey())).findFirst().get();
                    return updateAuthorityOnChanges(authorityEntity, stringListEntry.getValue());
                }).then();
    }

    Mono<Void> updateAuthorityOnChanges(AuthorityEntity authorityEntity, List<CompareUtil.Change> changes) {
        return Flux.fromIterable(changes)
                .flatMap(change -> {
                    switch (AuthorityRequest.Fields.valueOf(change.getField())) {
                        case description -> authorityEntity.setDescription(ChangeUtil.getStringElseConvert(change));
                    }
                    return Mono.empty();
                }).then();
    }
}
