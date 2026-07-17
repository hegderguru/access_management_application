package com.karur.access_management_application.security.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "permission", schema = "auth")
public class PermissionEntity {

    @Id
    private Long id;

    private String appId;
    private String fullyQualifiedFieldName;

    @Column("read_")
    private Boolean read;
    @Column("create_")
    private Boolean create;
    @Column("update_")
    private Boolean update;
    @Column("delete_")
    private Boolean delete;

    public String equalsId() {
        return appId + fullyQualifiedFieldName + read + create + update + delete;
    }
}
