package com.gad.msvc_customer.controller;

import com.gad.msvc_customer.dto.*;
import com.gad.msvc_customer.exception.*;
import com.gad.msvc_customer.model.Permission;
import com.gad.msvc_customer.model.Role;
import com.gad.msvc_customer.service.CustomerService;
import com.gad.msvc_customer.utils.UtilsMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID uuid;
    private CustomerDTO customerDTO;
    private CustomerRequest customerRequest;
    private CustomerUpdateRequest customerUpdateRequest;
    private CustomerRolesPermissionsDTO customerRolesPermissionsDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        Permission permission = new Permission();
        permission.setUuid(UUID.randomUUID());
        permission.setName("CREATE_TEST");

        Role role = new Role();
        role.setUuid(UUID.randomUUID());
        role.setName("ROLE_TESTER");
        role.setPermissions(Set.of(permission));

        uuid = UUID.randomUUID();

        customerDTO = new CustomerDTO(
                uuid.toString(),
                "John",
                "Doe"
        );

        customerRequest = new CustomerRequest(
                "John",
                "Connor",
                "password123",
                "johndoe@gmail.com",
                "987654321"
        );

        customerUpdateRequest = new CustomerUpdateRequest(
                "John",
                "Doe",
                "password",
                "john.doe@example.com",
                "987654321"
        );

        customerRolesPermissionsDTO = new CustomerRolesPermissionsDTO(
                uuid.toString(),
                "John",
                "john.doe@example.com",
                "password",
                Collections.singleton(new RolePermissionsDTO(
                        role.getName(),
                        Set.of(permission.getName()))
                )
        );
    }

    @Test
    @DisplayName("Should return status 200 and CustomerRolesPermissionsDTO with roles and permissions when email is valid format, existing and not is blank")
    void getCustomerByEmail_WhenEmailIsValidFormatAndExistingAndNotIsBlank_ReturnsStatus200AndCustomerRolesPermissionsDTO() throws Exception {
        String email = "john.doe@example.com";
        when(customerService.getCustomerRolesAndPermissionsByEmail(email))
                .thenReturn(customerRolesPermissionsDTO);

        mockMvc.perform(get("/api/v1/customers/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Customer found"))
                .andExpect(jsonPath("$.data.uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.data.name").value(customerRolesPermissionsDTO.name()))
                .andExpect(jsonPath("$.data.email").value(customerRolesPermissionsDTO.email()))
                .andExpect(jsonPath("$.data.password").value(customerRolesPermissionsDTO.password()))
                .andExpect(jsonPath("$.data.roles[0].role_name").value(customerRolesPermissionsDTO.roles().iterator().next().roleName()))
                .andExpect(jsonPath("$.data.roles[0].permissions[0]").value(customerRolesPermissionsDTO.roles().iterator().next().permissions().iterator().next()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).getCustomerRolesAndPermissionsByEmail(email);
    }

    @Test
    @DisplayName("Should return status 404 and throw CustomerNotFoundException when email is not existing")
    void getCustomerByEmail_WhenEmailsIsNotExisting_ReturnsStatus404AndThrowCustomerNotFoundException() throws Exception {
        String emailNotExisting = "pedro@gmail.com";
        when(customerService.getCustomerRolesAndPermissionsByEmail(emailNotExisting))
                .thenThrow(new CustomerNotFoundException("Customer with email " + emailNotExisting + " not found"));

        mockMvc.perform(get("/api/v1/customers/email/{email}", emailNotExisting))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Customer with email " + emailNotExisting + " not found"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).getCustomerRolesAndPermissionsByEmail(emailNotExisting);
    }

    @Test
    @DisplayName("Should return status 200 and CustomerDTO when uuid is valid format and existing")
    void getCustomerByUuId_WhenUuidIsValidFormatAndExisting_ReturnStatus200AndCustomer() throws Exception {
        UUID uuidValid = UUID.fromString(customerDTO.uuid());
        String uuidString = uuidValid.toString();
        when(customerService.getCustomerByUuid(uuidString))
                .thenReturn(customerDTO);

        mockMvc.perform(get("/api/v1/customers/{uuid}", uuidValid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Customer found"))
                .andExpect(jsonPath("$.data.uuid").value(uuidString))
                .andExpect(jsonPath("$.data.name").value("John"))
                .andExpect(jsonPath("$.data.last_name").value("Doe"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).getCustomerByUuid(uuidString);
    }

    @Test
    @DisplayName("Should return status 404 and throw CustomerNotFoundException when uuid is not existing")
    void getCustomerByUuid_WhenUuidIsNotExisting_ReturnsStatus404AndThrowCustomerNotFoundException() throws Exception {
        UUID uuidNotExisting = UUID.randomUUID();
        String uuidString = uuidNotExisting.toString();
        when(customerService.getCustomerByUuid(uuidString))
                .thenThrow(new CustomerNotFoundException("Customer with uuid " + uuidString + " not found"));

        mockMvc.perform(get("/api/v1/customers/{uuid}", uuidString))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Customer with uuid " + uuidString + " not found"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).getCustomerByUuid(uuidString);
    }

    @Test
    @DisplayName("Should return status 201 and CustomerDTO when CustomerRequest is valid")
    void createCustomer_WhenCustomerRequestIsValid_ReturnsStatus200AndCustomerDto() throws Exception {
        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenReturn(customerDTO);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Customer created successfully"))
                .andExpect(jsonPath("$.data.uuid").value(customerDTO.uuid()))
                .andExpect(jsonPath("$.data.name").value(customerDTO.name()))
                .andExpect(jsonPath("$.data.last_name").value(customerDTO.lastName()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).createCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Should return status 400 and throw MethodArgumentNotValidException when CustomerRequest is invalid")
    void createCustomer_WhenCustomerRequestIsInvalid_ReturnStatus400AndThrowMethodArgumentNotValidException() throws Exception {
        CustomerRequest customerRequestInvalid = new CustomerRequest(
                "Yo",
                "Do",
                "123",
                "invalid-email",
                "1234567891"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestInvalid)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation incorrect"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].messages[0]").value("Name must be between 3 and 50 characters"))
                .andExpect(jsonPath("$.errors[1].field").value("email"))
                .andExpect(jsonPath("$.errors[1].messages[0]").value("Email is not valid"))
                .andExpect(jsonPath("$.errors[2].field").value("password"))
                .andExpect(jsonPath("$.errors[2].messages[0]").value("Password must be between 5 and 50 characters"))
                .andExpect(jsonPath("$.errors[3].field").value("phone"))
                .andExpect(jsonPath("$.errors[3].messages[0]").value("Phone number must be exactly 9 digits"))
                .andExpect(jsonPath("$.errors[4].field").value("lastName"))
                .andExpect(jsonPath("$.errors[4].messages[0]").value("Last name must be between 3 and 50 characters"));

        verify(customerService, times(0)).createCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Should return status 400 and throw MethodArgumentNotValidException when CustomerRequest is blank")
    void createCustomer_WhenCustomerRequestIsBlank_ReturnStatus400AndThrowMethodArgumentNotValidException() throws Exception {
        CustomerRequest customerRequestInvalid = new CustomerRequest(
                " ",
                " ",
                " ",
                " ",
                " "
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestInvalid)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation incorrect"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].messages[0]").value("Name cannot be empty"))
                .andExpect(jsonPath("$.errors[1].field").value("email"))
                .andExpect(jsonPath("$.errors[1].messages[0]").value("Email cannot be empty"))
                .andExpect(jsonPath("$.errors[2].field").value("password"))
                .andExpect(jsonPath("$.errors[2].messages[0]").value("Password cannot be empty"))
                .andExpect(jsonPath("$.errors[3].field").value("phone"))
                .andExpect(jsonPath("$.errors[3].messages[0]").value("Phone cannot be empty"))
                .andExpect(jsonPath("$.errors[4].field").value("lastName"))
                .andExpect(jsonPath("$.errors[4].messages[0]").value("Last name cannot be empty"));

        verify(customerService, times(0)).createCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Should return status 400 and throw CustomerAlreadyExists when CustomerRequest have email Existing")
    void createCustomer_WhenCustomerRequestHaveEmailExisting_ReturnStatus400AndThrowCustomerAlreadyExists() throws Exception {
        CustomerRequest customerRequestInvalid = new CustomerRequest(
                "name test",
                "lastname test",
                "password",
                "testexisting@gmail.com",
                "987654321"
        );

        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenThrow(new CustomerAlreadyExists("Customer with email " + customerRequestInvalid.email() + " already exists"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestInvalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Customer with email " + customerRequestInvalid.email() + " already exists"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).createCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Should return status 404 and throw RoleNotFoundException when CustomerRequest is valid but ROLE_USER not found")
    void createCustomer_WhenCustomerRequestIsValidButRoleUserNotFound_ReturnsStatus404AndThrowRoleNotFoundException() throws Exception {
        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenThrow(new CustomerNotFoundException("Role ROLE_USER not found"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Role ROLE_USER not found"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).createCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Should return status 201 and CustomerDTO when CustomerUpdateRequest is valid")
    void updateCustomer_WhenCustomerUpdateRequestIsValid_ReturnsStatus200AndCustomerDto() throws Exception {
        String bearerToken = "Bearer token";

        when(customerService.updateCustomer(anyString(), any(CustomerUpdateRequest.class)))
                .thenReturn(customerDTO);

        mockMvc.perform(put("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearerToken)
                        .content(objectMapper.writeValueAsString(customerUpdateRequest)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Customer updated successfully"))
                .andExpect(jsonPath("$.data.uuid").value(customerDTO.uuid()))
                .andExpect(jsonPath("$.data.name").value(customerDTO.name()))
                .andExpect(jsonPath("$.data.last_name").value(customerDTO.lastName()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).updateCustomer(anyString(), any(CustomerUpdateRequest.class));
    }

    @ParameterizedTest
    @CsvSource({
            "JWT could not be decoded, 401",
            "Error loading public key, 401",
            "Invalid public key, 500"
    })
    @DisplayName("Should handle different Jwt exceptions")
    void updateCustomer_WithVariousExceptions_ReturnsExpectedStatusAndMessage(String exceptionMessage, int expectedStatus) throws Exception {
        String tokenBearer = "Bearer token";
        when(customerService.updateCustomer(anyString(), any())).thenThrow(new JwtDecodingException(exceptionMessage));

        mockMvc.perform(put("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", tokenBearer)
                        .content(objectMapper.writeValueAsString(customerUpdateRequest)))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.message").value(exceptionMessage))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").value(UtilsMethods.dateTimeNowFormatted()))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).updateCustomer(anyString(), any());
    }

    @Test
    @DisplayName("Should return status 500 and throw KeyFactoryCreationException when key factory for RSA cant be created")
    void updateCustomer_WhenKeyFactoryForRSACantBeCreated_ThrowsKeyFactoryCreationException() throws Exception {
        String tokenBearer = "Bearer token";
        when(customerService.updateCustomer(anyString(), any())).thenThrow(new KeyFactoryCreationException("Error creating Key factory for RSA"));

        mockMvc.perform(put("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", tokenBearer)
                        .content(objectMapper.writeValueAsString(customerUpdateRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Error creating Key factory for RSA"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").value(UtilsMethods.dateTimeNowFormatted()))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).updateCustomer(anyString(), any());
    }

    @Test
    @DisplayName("Should return status 400 and throw MethodArgumentNotValidException when CustomerUpdateRequest is invalid")
    void updateCustomer_WhenCustomerUpdateRequestIsInvalid_ReturnsStatus400AndThrowMethodArgumentNotValidException() throws Exception {
        String bearerToken = "Bearer token";
        CustomerUpdateRequest customerUpdateRequestInvalid = new CustomerUpdateRequest(
                "Yo",
                "Do",
                "123",
                "invalid-email",
                "1234567891a"
        );

        mockMvc.perform(put("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearerToken)
                        .content(objectMapper.writeValueAsString(customerUpdateRequestInvalid)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation incorrect"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].messages[0]").value("Name must be between 3 and 50 characters"))
                .andExpect(jsonPath("$.errors[1].field").value("email"))
                .andExpect(jsonPath("$.errors[1].messages[0]").value("Email is not valid"))
                .andExpect(jsonPath("$.errors[2].field").value("password"))
                .andExpect(jsonPath("$.errors[2].messages[0]").value("Password must be between 5 and 50 characters"))
                .andExpect(jsonPath("$.errors[3].field").value("phone"))
                .andExpect(jsonPath("$.errors[3].messages[0]").value("Phone number must be exactly 9 digits"))
                .andExpect(jsonPath("$.errors[4].field").value("lastName"))
                .andExpect(jsonPath("$.errors[4].messages[0]").value("Last name must be between 3 and 50 characters"));

        verify(customerService, times(0)).updateCustomer(anyString(), any(CustomerUpdateRequest.class));
    }

    @Test
    @DisplayName("Should return status 400 and throw MethodArgumentNotValidException when CustomerUpdateRequest is blank")
    void updateCustomer_WhenCustomerUpdateRequestIsBlank_ReturnStatus400AndThrowMethodArgumentNotValidException() throws Exception {
        String bearerToken = "Bearer token";
        CustomerRequest customerUpdateRequestInvalid = new CustomerRequest(
                " ",
                " ",
                " ",
                " ",
                " "
        );

        mockMvc.perform(put("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearerToken)
                        .content(objectMapper.writeValueAsString(customerUpdateRequestInvalid)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation incorrect"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].messages[0]").value("Name cannot be empty"))
                .andExpect(jsonPath("$.errors[1].field").value("email"))
                .andExpect(jsonPath("$.errors[1].messages[0]").value("Email cannot be empty"))
                .andExpect(jsonPath("$.errors[2].field").value("password"))
                .andExpect(jsonPath("$.errors[2].messages[0]").value("Password cannot be empty"))
                .andExpect(jsonPath("$.errors[3].field").value("phone"))
                .andExpect(jsonPath("$.errors[3].messages[0]").value("Phone cannot be empty"))
                .andExpect(jsonPath("$.errors[4].field").value("lastName"))
                .andExpect(jsonPath("$.errors[4].messages[0]").value("Last name cannot be empty"));

        verify(customerService, times(0)).createCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Should return status 404 and throw CustomerNotFoundException when CustomerUpdateRequest is valid but customer not found")
    void updateCustomer_WhenCustomerUpdateRequestIsValidButCustomerNotFound_ReturnsStatus404AndThrowCustomerNotFoundException() throws Exception {
        String bearerToken = "Bearer token";
        when(customerService.updateCustomer(anyString(), any(CustomerUpdateRequest.class)))
                .thenThrow(new CustomerNotFoundException("Customer with uuid " + uuid + " not found"));

        mockMvc.perform(put("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearerToken)
                        .content(objectMapper.writeValueAsString(customerUpdateRequest)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Customer with uuid " + uuid + " not found"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(customerService, times(1)).updateCustomer(anyString(), any(CustomerUpdateRequest.class));
    }
}