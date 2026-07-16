package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.compare.ChangeUtil;
import com.karur.access_management_application.security.compare.CompareUtil;
import com.karur.access_management_application.security.entity.join.AccessAuthorityEntity;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

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
