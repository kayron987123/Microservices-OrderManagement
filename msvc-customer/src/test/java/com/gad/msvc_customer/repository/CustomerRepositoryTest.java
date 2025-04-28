package com.gad.msvc_customer.repository;

import com.gad.msvc_customer.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private CustomerRepository customerRepository;

    private final String nonExistingEmail = "email@gmail.com";
    private Customer customer1;


    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();

        customer1 = new Customer();
        customer1.setUuid(UUID.randomUUID());
        customer1.setName("John");
        customer1.setLastName("Doe");
        customer1.setPassword("password");
        customer1.setEmail("qwerty@gmail.com");
        customer1.setPhone("1234567890");
        customer1.setRoles(null);

        customerRepository.save(customer1);
    }

    @Test
    @DisplayName("Should return customer when UUID exists")
    void findCustomerByUuid_WhenUuidExists_ReturnsCustomer() {
        Optional<Customer> customerOptional = customerRepository.findCustomerByUuid(customer1.getUuid());

        assertTrue(customerOptional.isPresent());
        assertEquals(customer1.getUuid(), customerOptional.get().getUuid());
        assertEquals(customer1.getName(), customerOptional.get().getName());
    }

    @Test
    @DisplayName("Should return optional empty when UUID does not exist")
    void findCustomerByUuid_WhenUuidDoesNotExist_ReturnsNull() {
        Optional<Customer> customerOptional = customerRepository.findCustomerByUuid(UUID.randomUUID());

        assertTrue(customerOptional.isEmpty());
    }

    @Test
    @DisplayName("Should return customer when email exists")
    void findCustomerByEmail_WhenEmailExists_ReturnsCustomer() {
        Optional<Customer> customerOptional = customerRepository.findCustomerByEmail(customer1.getEmail());

        assertTrue(customerOptional.isPresent());
        assertEquals(customer1.getEmail(), customerOptional.get().getEmail());
        assertEquals(customer1.getName(), customerOptional.get().getName());
    }

    @Test
    @DisplayName("Should return optional empty customer when email does not exist")
    void findCustomerByEmail_WhenEmailDoesNotExist_ReturnsNull() {

        Optional<Customer> customerOptional = customerRepository.findCustomerByEmail(nonExistingEmail);

        assertTrue(customerOptional.isEmpty());
    }

    @Test
    @DisplayName("Should return true when customer exists by email")
    void existsByEmail_WhenEmailExists_ReturnsTrue() {
        boolean exists = customerRepository.existsByEmail(customer1.getEmail());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when customer does not exist by email")
    void existsByEmail_WhenEmailDoesNotExists_ReturnsFalse() {
        boolean exists = customerRepository.existsByEmail(nonExistingEmail);

        assertFalse(exists);
    }
}