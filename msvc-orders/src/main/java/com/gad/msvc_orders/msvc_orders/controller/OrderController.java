package com.gad.msvc_orders.msvc_orders.controller;

import com.gad.msvc_orders.msvc_orders.dto.DataResponse;
import com.gad.msvc_orders.msvc_orders.dto.OrderDTO;
import com.gad.msvc_orders.msvc_orders.service.OrderService;
import com.gad.msvc_orders.msvc_orders.utils.Enums;
import com.gad.msvc_orders.msvc_orders.utils.UtilsMethods;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "Orders", description = "Rest Controller for Orders")
@Slf4j
@RequestMapping("api/v1/orders")
@RestController
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "Create Order",
            description = "Save a new order",
            tags = {"Create Order"},
            parameters = {
                    @Parameter(name = "Authorization", description = "Bearer token for authorization", required = true, in = ParameterIn.HEADER )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Order created",
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
    @PostMapping
    public ResponseEntity<DataResponse> createOrder(@RequestHeader("Authorization") String bearerToken) {
        OrderDTO orderDTO = orderService.createOrder(bearerToken);
        URI location = URI.create("api/v1/orders/" + orderDTO.uuidOrder());

        return ResponseEntity.created(location)
                .body(new DataResponse(
                        CREATED.value(),
                        "Order created",
                        orderDTO,
                        UtilsMethods.dateTimeNowFormatted(),
                        null
                ));
    }

    @Operation(
            summary = "Search order by uuid",
            description = "Find order by uuid",
            tags = {"Find Order"},
            parameters = {
                    @Parameter(name = "uuid", description = "uuid of the order to be searched", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid uuid format or empty",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "order not found with uuid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse> getOrderByUuid(@PathVariable @Pattern(regexp = Enums.PATTERN_REGEX, message = "Invalid UUID format")
                                                     @NotBlank(message = "Customer UUID cannot be empty") String uuid) {
        return ResponseEntity.ok()
                .body(new DataResponse(
                        OK.value(),
                        "Order found",
                        orderService.findByUuid(uuid),
                        UtilsMethods.dateTimeNowFormatted(),
                        null
                ));
    }

    @Operation(
            summary = "Update customer",
            description = "Update customer",
            tags = {"Update Customer"},
            parameters = {
                    @Parameter(name = "uuidOrderDetail", description = "uuid of the order detail to be updated", required = true),
                    @Parameter(name = "uuidOrder", description = "uuid of the order to be updated", required = true),
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "order updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid uuid format or empty in both parameters or one of them",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "order not found or feign order detail not found or feign order detail information could not be obtained",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
    @CircuitBreaker(name = "updateOrderCircuitBreaker", fallbackMethod = "updateOrderFallback")
    @Retry(name = "updateOrderRetry",  fallbackMethod = "updateOrderFallback")
    @PutMapping
    public ResponseEntity<DataResponse> updateOrder(@RequestParam @Pattern(regexp = Enums.PATTERN_REGEX, message = "Invalid UUID format")
                                                    @NotBlank(message = "Customer UUID cannot be empty") String uuidOrderDetail,
                                                    @RequestParam @Pattern(regexp = Enums.PATTERN_REGEX, message = "Invalid UUID format")
                                                    @NotBlank(message = "Customer UUID cannot be empty") String uuidOrder) {
        OrderDTO orderDTO = orderService.updateTotalPrice(uuidOrderDetail, uuidOrder);
        URI location = URI.create("api/v1/orders/" + orderDTO.uuidOrder());
        return ResponseEntity.created(location)
                .body(new DataResponse(
                        CREATED.value(),
                        "Order updated",
                        orderDTO,
                        UtilsMethods.dateTimeNowFormatted(),
                        null
                ));
    }

    @Operation(
            summary = "Delete order by uuid",
            description = "Delete order by uuid",
            tags = {"Delete Order"},
            parameters = {
                    @Parameter(name = "uuid", description = "uuid of the order to be deleted", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Customer found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid uuid format or empty",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "order not found with uuid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteOrder(@PathVariable @Pattern(regexp = Enums.PATTERN_REGEX, message = "Invalid UUID format")
                                            @NotBlank(message = "Customer UUID cannot be empty") String uuid) {
        orderService.deleteOrder(uuid);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<DataResponse> updateOrderFallback(String uuidOrderDetail, String uuidOrder, Throwable ex) {
        log.warn("Fallback triggered for updateOrder with uuidOrderDetail {} and uuidOrder {}. Reason: {}", uuidOrderDetail, uuidOrder, ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new DataResponse(
                        SERVICE_UNAVAILABLE.value(),
                        "Order service is currently unavailable. Please try again later.",
                        null,
                        UtilsMethods.dateTimeNowFormatted(),
                        Map.of("error", ex.getMessage())
                ));
    }
}
