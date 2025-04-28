package com.gad.msvc_customer.controller;

import com.gad.msvc_customer.dto.CustomerDTO;
import com.gad.msvc_customer.dto.CustomerRequest;
import com.gad.msvc_customer.dto.CustomerUpdateRequest;
import com.gad.msvc_customer.dto.DataResponse;
import com.gad.msvc_customer.service.CustomerService;
import com.gad.msvc_customer.utils.Enums;
import com.gad.msvc_customer.utils.UtilsMethods;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Customers", description = "Rest Controller for Customers")
@Validated
@RestController
@RequestMapping("api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @Operation(
            summary = "Search customer by email",
            description = "Find customer by email",
            tags = {"Find Customer"},
            parameters = {
                    @Parameter(name = "email", description = "email of the customer to be searched", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid email format or empty",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Customer not found with email",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
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

    @Operation(
            summary = "Search customer by uuid",
            description = "Find customer by uuid",
            tags = {"Find Customer"},
            parameters = {
                    @Parameter(name = "uuid", description = "uuid of the customer to be searched", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Customer found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid uuid format or uuid empty",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Customer not found with uuid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
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

    @Operation(
            summary = "Create customer",
            description = "Save customer",
            tags = {"Create Customer"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer request with data of the customer to be created",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Customer created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body or email already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Role default not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
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


    @Operation(
            summary = "Update customer",
            description = "Update customer",
            tags = {"Update Customer"},
            parameters = {
                    @Parameter(name = "Authorization", description = "Bearer token for authorization", required = true, in = ParameterIn.HEADER )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update customer request with data of the customer to be updated",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerUpdateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Customer updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Customer not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "JWT could not be decoded or Error loading public key",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Invalid public key or Error creating Key factory for RSA",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
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
