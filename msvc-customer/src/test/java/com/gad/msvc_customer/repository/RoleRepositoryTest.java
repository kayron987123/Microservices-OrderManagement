package com.gad.msvc_customer.repository;

import com.gad.msvc_customer.model.Role;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private RoleRepository roleRepository;

    private Role role;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();

        role = new Role();
        role.setUuid(UUID.randomUUID());
        role.setName("ROLE_TESTER");
        role.setPermissions(null);

        roleRepository.save(role);
    }

    @Test
    @DisplayName("Should return role when name exists")
    void findRoleByName_WhenNameExists_ReturnsRole() {
        Role foundRole = roleRepository.findRoleByName(role.getName()).orElse(null);

        assertNotNull(foundRole);
        assertEquals(role.getUuid(), foundRole.getUuid());
        assertEquals(role.getName(), foundRole.getName());
    }

    @Test
    @DisplayName("Should return null when name does not exist")
    void findRoleByName_WhenNameDoesNotExist_ReturnsNull() {
        String nonExistingRoleName = "ROLE_ADMIN";
        Role foundRole = roleRepository.findRoleByName(nonExistingRoleName).orElse(null);

        assertNull(foundRole);
    }
}