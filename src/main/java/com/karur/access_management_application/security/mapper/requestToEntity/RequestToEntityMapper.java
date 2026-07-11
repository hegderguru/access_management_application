package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.entity.AuthorityEntity;
import com.karur.access_management_application.security.entity.AccessEntity;
import com.karur.access_management_application.security.model.request.AccessorRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestToEntityMapper {

    @Autowired
    AccessRequestToEntityMapper accessRequestToEntityMapper;

    @Autowired
    AccessAuthorityRequestToEntityMapper accessAuthorityRequestToEntityMapper;

    public AccessEntity buildAccessorEntity(AccessorRequest accessorRequest) {
        AccessEntity accessEntity = accessRequestToEntityMapper.buildAccessorEntity(accessorRequest);
        accessEntity.setAccessGrantedAuthorities(accessAuthorityRequestToEntityMapper.buildAccessGrantedAuthorityEntities(accessorRequest));
        return accessEntity;
    }

    public AuthorityEntity buildAccessGrantedAuthorityEntity(AuthorityRequest authorityRequest){
        return accessAuthorityRequestToEntityMapper.buildAccessGrantedAuthorityEntity(authorityRequest);
    }

}
