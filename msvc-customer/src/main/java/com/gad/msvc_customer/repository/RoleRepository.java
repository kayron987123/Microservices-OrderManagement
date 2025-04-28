package com.gad.msvc_customer.repository;

import com.gad.msvc_customer.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findRoleByName(String name);
}
