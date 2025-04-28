package com.gad.msvc_customer.utils;

import com.gad.msvc_customer.dto.CustomerDTO;
import com.gad.msvc_customer.dto.CustomerRolesPermissionsDTO;
import com.gad.msvc_customer.dto.RolePermissionsDTO;
import com.gad.msvc_customer.model.Customer;
import com.gad.msvc_customer.model.Permission;
import jakarta.annotation.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public class CustomerMapper {
    private CustomerMapper() {
    }

    @Nullable
    public static CustomerDTO toDTO(@Nullable final Customer customer) {
        if (customer == null) return null;
        return new CustomerDTO(
                UtilsMethods.convertUUIDToString(customer.getUuid()),
                customer.getName(),
                customer.getLastName()
        );
    }

    public static CustomerRolesPermissionsDTO toRolesAndPermissionsDTO(final Customer customer){
        Set<RolePermissionsDTO> roles = customer.getRoles().stream()
                .map(role -> new RolePermissionsDTO(
                        role.getName(),
                        role.getPermissions().stream()
                                .map(Permission::getName)
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toSet());
        return new CustomerRolesPermissionsDTO(
                UtilsMethods.convertUUIDToString(customer.getUuid()),
                customer.getName(),
                customer.getEmail(),
                customer.getPassword(),
                roles
        );
    }
}
