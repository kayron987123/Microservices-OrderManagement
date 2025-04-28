package com.gad.msvc_customer.dto;

import java.util.Set;

public record CustomerRolesPermissionsDTO(
        String uuid,
        String name,
        String email,
        String password,
        Set<RolePermissionsDTO> roles
) {
}
