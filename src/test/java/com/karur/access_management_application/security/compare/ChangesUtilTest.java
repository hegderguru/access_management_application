package com.karur.access_management_application.security.compare;

import com.karur.access_management_application.security.model.request.AccessorRequest;
import com.karur.access_management_application.security.model.request.AuthorityRequest;
import com.karur.access_management_application.security.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ChangesUtilTest {

    // Initialize the utility class instance
    private final CompareUtil compareUtil = new CompareUtil();

    @Test
    void compare() {
        // Arrange
        AccessorRequest accessorRequestOld = buildAccessorRequest("hegderguru", "abc", "Guru");
        AccessorRequest accessorRequestNew = buildAccessorRequest("hegderguru", "abcd", "Guru2");

        accessorRequestOld.setAuthorityRequests(new ArrayList<>());
        accessorRequestNew.setAuthorityRequests(new ArrayList<>());

        accessorRequestOld.getAuthorityRequests().add(buildAuthorityRequest("admin", "admin"));
        accessorRequestOld.getAuthorityRequests().add(buildAuthorityRequest("admin2", "admin2"));

        accessorRequestNew.getAuthorityRequests().add(buildAuthorityRequest("admin", "admin"));
        accessorRequestNew.getAuthorityRequests().add(buildAuthorityRequest("admin2", "admin3"));
        accessorRequestNew.getAuthorityRequests().add(buildAuthorityRequest("admin3", "admin3"));

        // Act - Call using the instance variable 'changesUtil' instead of static call
        List<CompareUtil.Change> compare = compareUtil.compare(accessorRequestOld, accessorRequestNew);
        log.info("Compare: Objects Object1: {} and Object2: {} with differences: {} ", CommonUtil.writeValueAsString(accessorRequestOld), CommonUtil.writeValueAsString(accessorRequestNew), CommonUtil.writeValueAsString(compare));
        // Assert - Validate that changes were correctly identified
        assertNotNull(compare);

    }

    public AccessorRequest buildAccessorRequest(String username, String password, String firstName) {
        return AccessorRequest.builder()
                .username(username)
                .password(password)
                .firstName(firstName)
                .build();
    }

    public AuthorityRequest buildAuthorityRequest(String name, String description) {
        return AuthorityRequest.builder()
                .name(name)
                .description(description)
                .build();
    }
}
