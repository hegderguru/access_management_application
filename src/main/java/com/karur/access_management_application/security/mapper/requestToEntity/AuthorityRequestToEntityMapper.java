package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.join.AccessAuthorityEntity;
import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
}
