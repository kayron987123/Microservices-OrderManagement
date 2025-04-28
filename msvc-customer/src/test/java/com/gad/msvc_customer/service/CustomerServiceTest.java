package com.gad.msvc_customer.service;

import com.gad.msvc_customer.dto.CustomerDTO;
import com.gad.msvc_customer.dto.CustomerRequest;
import com.gad.msvc_customer.dto.CustomerRolesPermissionsDTO;
import com.gad.msvc_customer.dto.CustomerUpdateRequest;
import com.gad.msvc_customer.exception.CustomerAlreadyExists;
import com.gad.msvc_customer.exception.CustomerNotFoundException;
import com.gad.msvc_customer.exception.RoleNotFoundException;
import com.gad.msvc_customer.model.Customer;
import com.gad.msvc_customer.model.Permission;
import com.gad.msvc_customer.model.Role;
import com.gad.msvc_customer.repository.CustomerRepository;
import com.gad.msvc_customer.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        Permission permission = new Permission();
        permission.setUuid(UUID.randomUUID());
        permission.setName("CREATE_TEST");

        Role role = new Role();
        role.setUuid(UUID.randomUUID());
        role.setName("ROLE_TESTER");
        role.setPermissions(Set.of(permission));

        customer = new Customer();
        customer.setUuid(UUID.randomUUID());
        customer.setName("Test Name");
        customer.setLastName("Test Last Name");
        customer.setEmail("test@gmail.com");
        customer.setPassword("testPassword");
        customer.setPhone("1234567890");
        customer.setRoles(Set.of(role));
    }

    @Test
    @DisplayName("Should return customer with roles and permissions when email exists")
    void getCustomerRolesAndPermissionsByEmail_WhenEmailExists_ReturnsCustomer() {
        String email = customer.getEmail();
        when(customerRepository.findCustomerByEmail(email)).thenReturn(Optional.of(customer));

        CustomerRolesPermissionsDTO customerFound = customerService.getCustomerRolesAndPermissionsByEmail(email);

        assertNotNull(customerFound);
        assertEquals(customer.getUuid(), UUID.fromString(customerFound.uuid()));
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when email does not exist")
    void getCustomerRolesAndPermissionsByEmail_WhenEmailDoesNotExist_ThrowCustomerNotFoundException() {
        String nonExistingEmail = "qwerty@gmail.com";
        when((customerRepository.findCustomerByEmail(nonExistingEmail))).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class, () ->
                customerService.getCustomerRolesAndPermissionsByEmail(nonExistingEmail));

        assertEquals("Customer with email " + nonExistingEmail + " not found", exception.getMessage());
        verify(customerRepository, times(1)).findCustomerByEmail(nonExistingEmail);
    }

    @Test
    @DisplayName("Should return customer when UUID exists")
    void getCustomerByUuid_WhenUuidExists_ReturnsCustomer() {
        UUID uuid = customer.getUuid();
        when(customerRepository.findCustomerByUuid(uuid)).thenReturn(Optional.of(customer));

        CustomerDTO foundCustomer = customerService.getCustomerByUuid(uuid.toString());

        assertNotNull(foundCustomer);
        assertEquals(customer.getUuid(), UUID.fromString(foundCustomer.uuid()));
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when UUID does not exist")
    void getCustomerByUuid_WhenUuidDoesNotExist_ThrowCustomerNotFoundException() {
        UUID nonExistingUuid = UUID.randomUUID();
        when(customerRepository.findCustomerByUuid(nonExistingUuid)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class, () ->
                customerService.getCustomerByUuid(nonExistingUuid.toString()));

        assertEquals("Customer with uuid " + nonExistingUuid + " not found", exception.getMessage());
        verify(customerRepository, times(1)).findCustomerByUuid(nonExistingUuid);
    }

    @Test
    @DisplayName("Should create customer when CustomerRequest is valid")
    void createCustomer_WhenCustomerRequestIsValid_ReturnsCustomerDto() {
        CustomerRequest customerRequest = new CustomerRequest(
                "Test Name",
                "Test Last Name",
                "password",
                "email@gmail.com",
                "1234567890"
        );

        Role defaultRole = new Role();
        defaultRole.setUuid(UUID.randomUUID());
        defaultRole.setName("ROLE_USER");

        Customer customerToSave = new Customer();
        customerToSave.setUuid(UUID.randomUUID());
        customerToSave.setName(customerRequest.name());
        customerToSave.setLastName(customerRequest.lastName());
        customerToSave.setEmail(customerRequest.email());
        customerToSave.setPassword("encodedPassword");
        customerToSave.setPhone(customerRequest.phone());
        customerToSave.setRoles(Set.of(defaultRole));

        when(roleRepository.findRoleByName(defaultRole.getName())).thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode(customerRequest.password())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customerToSave);

        CustomerDTO createdCustomer = customerService.createCustomer(customerRequest);

        assertNotNull(createdCustomer);
        assertEquals(customerToSave.getUuid(), UUID.fromString(createdCustomer.uuid()));
        assertEquals(customerToSave.getName(), createdCustomer.name());
        assertEquals(customerToSave.getLastName(), createdCustomer.lastName());
        verify(roleRepository, times(1)).findRoleByName(defaultRole.getName());
        verify(passwordEncoder, times(1)).encode(customerRequest.password());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when email already exists")
    void createCustomer_WhenEmailAlreadyExists_ThrowCustomerAlreadyExists() {
        CustomerRequest customerRequest = new CustomerRequest(
                "Test Name",
                "Test Last Name",
                "password",
                "email@gmail.com",
                "1234567890"
        );

        Role defaultRole = new Role();
        defaultRole.setUuid(UUID.randomUUID());
        defaultRole.setName("ROLE_USER");

        Customer customerToSave = new Customer();
        customerToSave.setUuid(UUID.randomUUID());
        customerToSave.setName(customerRequest.name());
        customerToSave.setLastName(customerRequest.lastName());
        customerToSave.setEmail(customerRequest.email());
        customerToSave.setPassword("encodedPassword");
        customerToSave.setPhone(customerRequest.phone());
        customerToSave.setRoles(Set.of(defaultRole));

        when(customerRepository.existsByEmail(customerRequest.email())).thenReturn(true);

        Exception exception = assertThrows(CustomerAlreadyExists.class, () ->
                customerService.createCustomer(customerRequest));

        assertEquals("Customer with email " + customerRequest.email() + " already exists", exception.getMessage());
        verify(customerRepository, times(1)).existsByEmail(customerRequest.email());
        verify(roleRepository, never()).findRoleByName(defaultRole.getName());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when role does not exist")
    void createCustomer_WhenRoleDoesNotExist_ThrowRoleNotFoundException() {
        CustomerRequest customerRequest = new CustomerRequest(
                "Test Name",
                "Test Last Name",
                "password",
                "email@gmail.com",
                "1234567890"
        );

        Role defaultRole = new Role();
        defaultRole.setUuid(UUID.randomUUID());
        defaultRole.setName("ROLE_USER");

        Customer customerToSave = new Customer();
        customerToSave.setUuid(UUID.randomUUID());
        customerToSave.setName(customerRequest.name());
        customerToSave.setLastName(customerRequest.lastName());
        customerToSave.setEmail(customerRequest.email());
        customerToSave.setPassword("encodedPassword");
        customerToSave.setPhone(customerRequest.phone());
        customerToSave.setRoles(Set.of(defaultRole));

        when(roleRepository.findRoleByName(defaultRole.getName())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RoleNotFoundException.class, () ->
                customerService.createCustomer(customerRequest));

        assertEquals("Role " + defaultRole.getName() + " not found", exception.getMessage());
        verify(customerRepository, times(1)).existsByEmail(customerRequest.email());
        verify(roleRepository, times(1)).findRoleByName(defaultRole.getName());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should update customer when UUID exists and CustomerUpdateRequest is valid")
    void updateCustomer_WhenUuidExistsAndCustomerUpdateRequestIsValid_ReturnsCustomerDto() {
        String bearerToken = "Bearer token valid";
        CustomerUpdateRequest customerToUpdateRequest = new CustomerUpdateRequest("Updated  name",
                "Updated Last Name", "newPassword", "updated@gmail.com", "987654321");

        when(jwtService.getUserUuidFromJwt(anyString())).thenReturn(customer.getUuid());
        when(customerRepository.findCustomerByUuid(customer.getUuid())).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode(customerToUpdateRequest.password())).thenReturn("newEncodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO customerDTO = customerService.updateCustomer(bearerToken, customerToUpdateRequest);

        assertNotNull(customerDTO);
        assertEquals(customer.getUuid(), UUID.fromString(customerDTO.uuid()));
        assertEquals(customerToUpdateRequest.name(), customerDTO.name());
        assertEquals(customerToUpdateRequest.lastName(), customerDTO.lastName());
        verify(jwtService, times(1)).getUserUuidFromJwt(bearerToken);
        verify(customerRepository, times(1)).findCustomerByUuid(customer.getUuid());
        verify(passwordEncoder, times(1)).encode(customerToUpdateRequest.password());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when UUID does not exist")
    void updateCustomer_WhenUuidDoesNotExist_ThrowCustomerNotFoundException() {
        String bearerToken = "Bearer token valid";
        UUID nonExistingUuid = UUID.randomUUID();
        CustomerUpdateRequest customerToUpdateRequest = new CustomerUpdateRequest("Updated  name",
                "Updated Last Name", "newPassword", "updated@gmail.com", "987654321");

        when(jwtService.getUserUuidFromJwt(bearerToken)).thenReturn(nonExistingUuid);
        when(customerRepository.findCustomerByUuid(nonExistingUuid)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class, () ->
                customerService.updateCustomer(bearerToken, customerToUpdateRequest));

        assertEquals("Customer with uuid " + nonExistingUuid + " not found", exception.getMessage());
        verify(jwtService, times(1)).getUserUuidFromJwt(bearerToken);
        verify(customerRepository, times(1)).findCustomerByUuid(nonExistingUuid);
        verify(passwordEncoder, never()).encode(customerToUpdateRequest.password());
        verify(customerRepository, never()).save(any(Customer.class));
    }
}