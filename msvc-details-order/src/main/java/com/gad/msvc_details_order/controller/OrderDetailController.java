package com.gad.msvc_details_order.controller;

import com.gad.msvc_details_order.dto.CreateOrderDetailRequest;
import com.gad.msvc_details_order.dto.DataResponse;
import com.gad.msvc_details_order.dto.OrderDetailDTO;
import com.gad.msvc_details_order.service.OrderDetailService;
import com.gad.msvc_details_order.utils.Enums;
import com.gad.msvc_details_order.utils.FormatterDateTime;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/order-details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @CircuitBreaker(name = "createOrderDetailCircuitBreaker", fallbackMethod = "createOrderDetailFallback")
    @Retry(name = "createOrderDetailRetry", fallbackMethod = "createOrderDetailFallback")
    @PostMapping
    public ResponseEntity<DataResponse> createOrderDetail(@RequestBody @Valid CreateOrderDetailRequest createOrderDetailRequest) {
        if ("00000000-0000-0000-0000-000000000000".equals(createOrderDetailRequest.uuidOrder()) || "00000000-0000-0000-0000-000000000000".equals(createOrderDetailRequest.uuidProduct())) {
            throw new IllegalArgumentException("Simulated failure for Circuit Breaker");
        }
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


    @CircuitBreaker(name = "getOrderDetailByUuidCircuitBreaker", fallbackMethod = "getOrderDetailByUuidFallback")
    @Retry(name = "getOrderDetailByUuidRetry", fallbackMethod = "getOrderDetailByUuidFallback")
    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse> getOrderDetailByUuid(@PathVariable @Pattern(regexp = Enums.PATTERN_REGEX, message = "Invalid UUID format")
                                                             @NotBlank(message = "Customer UUID cannot be empty") String uuid) {
        return ResponseEntity.ok().body(new DataResponse(
                HttpStatus.OK.value(),
                "Order detail found",
                orderDetailService.findOrderDetailByUuid(uuid),
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

    public ResponseEntity<DataResponse> getOrderDetailByUuidFallback(String uuid, Throwable ex) {
        log.warn("Fallback triggered for getOrderDetailByUuid with uuid {}. Reason: {}", uuid, ex.getMessage());
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
