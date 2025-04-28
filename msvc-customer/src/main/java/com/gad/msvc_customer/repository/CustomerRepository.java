package com.gad.msvc_customer.repository;

import com.gad.msvc_customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findCustomerByUuid(UUID uuid);
    Optional<Customer> findCustomerByEmail(String email);
    boolean existsByEmail(String email);
}
