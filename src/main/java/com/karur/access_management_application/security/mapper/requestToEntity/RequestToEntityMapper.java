package com.karur.access_management_application.security.mapper.requestToEntity;

import com.karur.access_management_application.security.authentication.model.AccessorEntity;
import com.karur.access_management_application.security.model.request.AccessorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RequestToEntityMapper {

    @Autowired
    AccessRequestToEntityMapper accessRequestToEntityMapper;

    @Autowired
    AccessAuthorityRequestToEntityMapper accessAuthorityRequestToEntityMapper;

    public AccessorEntity buildAccessorEntity(AccessorRequest accessorRequest) {
        AccessorEntity accessorEntity = accessRequestToEntityMapper.buildAccessorEntity(accessorRequest);
        accessorEntity.setAccessGrantedAuthorities(accessAuthorityRequestToEntityMapper.buildAccessGrantedAuthorityEntities(accessorRequest));
        return accessorEntity;
    }

}
