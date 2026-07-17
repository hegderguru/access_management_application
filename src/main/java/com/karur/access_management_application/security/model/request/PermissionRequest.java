package com.karur.access_management_application.security.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.karur.access_management_application.security.compare.DiffId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldNameConstants(asEnum = true)
public class PermissionRequest {

    @DiffId
    private String appId;

    @DiffId
    private String fullyQualifiedFieldName;

    private Boolean read;
    private Boolean create;
    private Boolean update;
    private Boolean delete;

    public String equalsId() {
        return appId + fullyQualifiedFieldName + read + create + update + delete;
    }
}
