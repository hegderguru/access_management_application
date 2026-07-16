package com.karur.access_management_application.security.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "permission",schema = "auth")
public class PermissionEntity {

    @Id
    private Long id;
    private String fullyQualifiedFieldName;

    private Boolean read_;
    private Boolean create_;
    private Boolean update_;
    private Boolean delete_;
}
