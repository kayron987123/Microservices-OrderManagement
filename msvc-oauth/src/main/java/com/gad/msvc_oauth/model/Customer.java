package com.gad.msvc_oauth.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
public class Customer {
    private UUID uuid;
    private String name;
    private String email;
    private String password;
    private Set<Roles> roles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(uuid, customer.uuid) && Objects.equals(name, customer.name) && Objects.equals(email, customer.email) && Objects.equals(password, customer.password) && Objects.equals(roles, customer.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, email, password, roles);
    }
}
