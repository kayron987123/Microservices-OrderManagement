package com.gad.msvc_details_order.controller;

import com.gad.msvc_details_order.dto.CreateOrderDetailRequest;
import com.gad.msvc_details_order.dto.DataResponse;
import com.gad.msvc_details_order.dto.OrderDetailDTO;
import com.gad.msvc_details_order.service.OrderDetailService;
import com.gad.msvc_details_order.utils.Enums;
import com.gad.msvc_details_order.utils.FormatterDateTime;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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
import java.util.UUID;

@Slf4j
@Validated
@RequestMapping("/api/v1/order-details")
@RestController
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @Operation(
            summary = "Create Order Detail",
            description = "Save a new order detail",
            tags = {"Create Order Detail"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer request with data of the customer to be created",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateOrderDetailRequest.class)
                    )
            ),
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
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "feign order not found or feign order cannot be obtained or order not found" +
                                    ", feign product not found or feign product cannot be obtained or product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "The amount entered exceeds the stock of the product",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
    @CircuitBreaker(name = "createOrderDetailCircuitBreaker", fallbackMethod = "createOrderDetailFallback")
    @Retry(name = "createOrderDetailRetry", fallbackMethod = "createOrderDetailFallback")
    @PostMapping
    public ResponseEntity<DataResponse> createOrderDetail(@RequestBody @Valid CreateOrderDetailRequest createOrderDetailRequest) {
        OrderDetailDTO orderDetailDTO = orderDetailService.createOrderDetail(createOrderDetailRequest);
        URI location = URI.create("api/v1/order-details/" + orderDetailDTO.uuidDetail());

        return ResponseEntity.created(location).body(new DataResponse(
                HttpStatus.CREATED.value(),
                "Order detail created",
                orderDetailDTO,
                FormatterDateTime.dateTimeNowFormatted(),
                null
        ));
    }

    @Operation(
            summary = "Search order detail by uuid",
            description = "Find order detail by uuid",
            tags = {"Search Order Detail"},
            parameters = {
                    @Parameter(name = "uuid", description = "uuid of the order detail to be searched", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Order detail found",
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
                            description = "Order detail not found with uuid or feign product not found or feign product cannot be obtained",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse> getOrderDetailByUuid(@PathVariable @Pattern(regexp = Enums.PATTERN_REGEX, message = "Invalid UUID format")
                                                             @NotBlank(message = "Customer UUID cannot be empty") String uuid) {
        UUID uuidOrderDetail = UUID.fromString(uuid);
        return ResponseEntity.ok().body(new DataResponse(
                HttpStatus.OK.value(),
                "Order detail found",
                orderDetailService.findOrderDetailByUuid(uuidOrderDetail),
                FormatterDateTime.dateTimeNowFormatted(),
                null
        ));
    }

    public ResponseEntity<DataResponse> createOrderDetailFallback(CreateOrderDetailRequest createOrderDetailRequest, Throwable ex) {
        log.warn("Fallback triggered for createOrderDetail with request {}. Reason: {}", createOrderDetailRequest, ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new DataResponse(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Order Detail service is currently unavailable. Please try again later.",
                        null,
                        FormatterDateTime.dateTimeNowFormatted(),
                        Map.of("error", ex.getMessage())
                ));
    }
}
