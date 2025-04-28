package com.gad.msvc_customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record RolePermissionsDTO(
        @JsonProperty("role_name")
        String roleName,
        Set<String> permissions
) {
}
