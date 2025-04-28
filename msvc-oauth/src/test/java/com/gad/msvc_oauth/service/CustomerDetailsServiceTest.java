package com.gad.msvc_oauth.service;

import com.gad.msvc_oauth.model.Customer;
import com.gad.msvc_oauth.model.Roles;
import com.gad.msvc_oauth.service.feign.CustomerServiceFeign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerDetailsServiceTest {
    @Mock
    private CustomerServiceFeign customerServiceFeign;

    @InjectMocks
    private CustomerDetailsService customerDetailsService;
    private Customer customer;

    @BeforeEach
    void setUp() {
        Roles role = new Roles();
        role.setRoleName("ROLE_TEST");
        role.setPermissions(Set.of("CREATE_TEST"));

        customer = new Customer();
        customer.setUuid(UUID.randomUUID());
        customer.setName("Test Name");
        customer.setEmail("test@gmail.com");
        customer.setPassword("testPassword");
        customer.setRoles(Set.of(role));
    }

    @Test
    @DisplayName("Should return customer when the username exists")
    void loadUserByUsername() {
        String username = "usernameTest";

        when(customerServiceFeign.findByEmail(anyString())).thenReturn(customer);

        User user = (User) customerDetailsService.loadUserByUsername(username);

        assertNotNull(user);
        assertEquals(customer.getEmail(), user.getUsername());
        assertEquals(customer.getPassword(), user.getPassword());
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isAccountNonLocked());

        verify(customerServiceFeign, times(1)).findByEmail(username);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when the username does not exist")
    void loadUserByUsername_UserNotFound() {
        String username = "nonExistentUser";

        when(customerServiceFeign.findByEmail(anyString())).thenReturn(null);

        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                customerDetailsService.loadUserByUsername(username));

        assertEquals("Customer with email " + username + " not found", exception.getMessage());
        verify(customerServiceFeign, times(1)).findByEmail(username);
    }
}