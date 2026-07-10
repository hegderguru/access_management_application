package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.authentication.model.AccessGrantedAuthorityEntity;
import com.karur.access_management_application.security.model.request.AccessorRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccessAuthorityRequestToEntityMapper {

    public List<AccessGrantedAuthorityEntity> buildAccessGrantedAuthorityEntities(AccessorRequest accessorRequest){
        return accessorRequest.getAuthorityRequests().stream().map(this::buildAccessGrantedAuthorityEntity).toList();
    }

    private AccessGrantedAuthorityEntity buildAccessGrantedAuthorityEntity(AuthorityRequest authorityRequest) {
        return AccessGrantedAuthorityEntity.builder()
                .accessRoleEntities(new ArrayList<>())
                .name(authorityRequest.getName())
                .build();
    }
}
