package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.authentication.model.AuthorityEntity;
import com.karur.access_management_application.security.model.request.AccessorRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccessAuthorityRequestToEntityMapper {

    public List<AuthorityEntity> buildAccessGrantedAuthorityEntities(AccessorRequest accessorRequest){
        return accessorRequest.getAuthorityRequests().stream().map(this::buildAccessGrantedAuthorityEntity).toList();
    }

    public AuthorityEntity buildAccessGrantedAuthorityEntity(AuthorityRequest authorityRequest) {
        return AuthorityEntity.builder()
                .accessRoleEntities(new ArrayList<>())
                .name(authorityRequest.getName())
                .description(authorityRequest.getDescription())
                .build();
    }
}
