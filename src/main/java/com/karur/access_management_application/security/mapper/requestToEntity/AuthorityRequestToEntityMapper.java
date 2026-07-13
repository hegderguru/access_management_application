package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AccessAuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorityRequestToEntityMapper {

    public List<AccessAuthorityEntity> buildAccessAuthorityEntities(AccessEntity accessEntity, List<AuthorityEntity> authorityEntities) {
        return authorityEntities.stream().map(authorityEntity -> buildAccessAuthorityEntity(accessEntity.getId(), authorityEntity)).toList();
    }

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
}
