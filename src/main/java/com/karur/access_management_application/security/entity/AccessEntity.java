package com.karur.access_management_application.security.entity;

import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@Data
@Table(value = "access",schema = "auth")
public class AccessEntity {

    @Id
    private Long id;

    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private boolean accessExpired;
    private boolean accessLocked;
    private boolean accessEnabled;
    private boolean credentialsExpired;

    @Transient
    private List<AuthorityEntity> authorityEntities;

    public List<AuthorityEntity> accessGrantedAuthorities(){
        return authorityEntities;
    }
}
