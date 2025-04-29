package com.gad.msvc_customer.controller;

import com.gad.msvc_customer.dto.CustomerDTO;
import com.gad.msvc_customer.dto.CustomerRequest;
import com.gad.msvc_customer.dto.CustomerUpdateRequest;
import com.gad.msvc_customer.dto.DataResponse;
import com.gad.msvc_customer.service.CustomerService;
import com.gad.msvc_customer.utils.Enums;
import com.gad.msvc_customer.utils.UtilsMethods;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/email/{email}")
    public ResponseEntity<DataResponse> getCustomerByEmail(@PathVariable @Email(message = "Email is not valid") @NotBlank(message = "Phone cannot be empty") String email) {
        return ResponseEntity.ok().body(new DataResponse(
                OK.value(),
                "Customer found",
                customerService.getCustomerRolesAndPermissionsByEmail(email),
                UtilsMethods.dateTimeNowFormatted(),
                null
        ));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse> getCustomerByUuid(@PathVariable @Pattern(regexp = Enums.PATTERN_REGEX, message = "Invalid UUID format")
                                                              @NotBlank(message = "Customer UUID cannot be empty") String uuid) {
        return ResponseEntity.ok().body(new DataResponse(
                OK.value(),
                "Customer found",
                customerService.getCustomerByUuid(uuid),
                UtilsMethods.dateTimeNowFormatted(),
                null
        ));
    }

    @PostMapping
    public ResponseEntity<DataResponse> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        CustomerDTO customerDTO = customerService.createCustomer(customerRequest);
        URI location = URI.create("api/v1/customers/" + customerDTO.uuid());
        return ResponseEntity.created(location)
                .body(new DataResponse(
                        CREATED.value(),
                        "Customer created successfully",
                        customerDTO,
                        UtilsMethods.dateTimeNowFormatted(),
                        null
                ));
    }

    @PutMapping
    public ResponseEntity<DataResponse> updateCustomer(@RequestHeader("Authorization") String bearerToken,
                                                       @Valid @RequestBody CustomerUpdateRequest customerRequest) {
        CustomerDTO customerDb = customerService.updateCustomer(bearerToken, customerRequest);
        URI location = URI.create("api/v1/customers/" + customerDb.uuid());
        return ResponseEntity.created(location)
                .body(new DataResponse(
                        CREATED.value(),
                        "Customer updated successfully",
                        customerDb,
                        UtilsMethods.dateTimeNowFormatted(),
                        null
                ));
    }
}
