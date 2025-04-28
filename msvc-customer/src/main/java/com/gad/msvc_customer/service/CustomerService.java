package com.gad.msvc_customer.service;

import com.gad.msvc_customer.dto.CustomerDTO;
import com.gad.msvc_customer.dto.CustomerRequest;
import com.gad.msvc_customer.dto.CustomerRolesPermissionsDTO;
import com.gad.msvc_customer.dto.CustomerUpdateRequest;
import com.gad.msvc_customer.exception.CustomerAlreadyExists;
import com.gad.msvc_customer.exception.CustomerNotFoundException;
import com.gad.msvc_customer.exception.RoleNotFoundException;
import com.gad.msvc_customer.model.Customer;
import com.gad.msvc_customer.model.Role;
import com.gad.msvc_customer.repository.CustomerRepository;
import com.gad.msvc_customer.repository.RoleRepository;
import com.gad.msvc_customer.utils.CustomerMapper;
import com.gad.msvc_customer.utils.UtilsMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private static final String ROLE_DEFAULT = "ROLE_USER";
    private static final String MESSAGE_NOT_FOUND = " not found";

    @Cacheable(value = "User'sRolesAndPermissions", key = "#email")
    @Transactional(readOnly = true)
    public CustomerRolesPermissionsDTO getCustomerRolesAndPermissionsByEmail(String email) {
        Customer customer = customerRepository.findCustomerByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Customer with email " + email + MESSAGE_NOT_FOUND));
        return CustomerMapper.toRolesAndPermissionsDTO(customer);
    }

    @Cacheable(value = "CustomerByUuid", key = "#uuid")
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByUuid(String uuid) {
        UUID uuidCustomer = UtilsMethods.convertStringToUUID(uuid);
        Customer customer = customerRepository.findCustomerByUuid(uuidCustomer).orElseThrow(() -> new CustomerNotFoundException("Customer with uuid " + uuidCustomer + MESSAGE_NOT_FOUND));
        return CustomerMapper.toDTO(customer);
    }

    @Transactional
    public CustomerDTO createCustomer(CustomerRequest customerRequest) {
        validateCustomerExistsByEmail(customerRequest.email());
        Role defaultRole = roleRepository.findRoleByName(ROLE_DEFAULT).orElseThrow(() -> new RoleNotFoundException("Role " + ROLE_DEFAULT + MESSAGE_NOT_FOUND));

        Set<Role> roles = Set.of(defaultRole);

        Customer customer = new Customer();
        customer.setName(customerRequest.name());
        customer.setLastName(customerRequest.lastName());
        customer.setPassword(passwordEncoder.encode(customerRequest.password()));
        customer.setEmail(customerRequest.email());
        customer.setPhone(customerRequest.phone());
        customer.setRoles(roles);

        return CustomerMapper.toDTO(customerRepository.save(customer));
    }


    @Caching(evict = {
            @CacheEvict(value = "CustomerByUuid", key = "#bearerToken"),
            @CacheEvict(value = "User'sRolesAndPermissions", key = "#customerUpdateRequest.email")
    })
    @Transactional
    public CustomerDTO updateCustomer(String bearerToken, CustomerUpdateRequest customerUpdateRequest) {
        String uuid = jwtService.getUserUuidFromJwt(bearerToken).toString();
        UUID customerUuid = UUID.fromString(uuid);
        Customer customer = customerRepository.findCustomerByUuid(customerUuid).orElseThrow(() -> new CustomerNotFoundException("Customer with uuid " + customerUuid + MESSAGE_NOT_FOUND));
        updateCustomerFields(customerUpdateRequest, customer);

        return CustomerMapper.toDTO(customerRepository.save(customer));
    }

    private void updateCustomerFields(CustomerUpdateRequest request, Customer customer) {
        if (!customer.getName().equals(request.name())) {
            customer.setName(request.name());
        }

        if (!customer.getLastName().equals(request.lastName())) {
            customer.setLastName(request.lastName());
        }

        if (!customer.getEmail().equals(request.email())) {
            customer.setEmail(request.email());
        }

        if (!passwordEncoder.matches(request.password(), customer.getPassword())) {
            customer.setPassword(passwordEncoder.encode(request.password()));
        } else {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }

        if (!customer.getPhone().equals(request.phone())) {
            customer.setPhone(request.phone());
        }
    }

    private void validateCustomerExistsByEmail(String email) {
        if (customerRepository.existsByEmail(email)) {
            throw new CustomerAlreadyExists("Customer with email " + email + " already exists");
        }
    }
}
