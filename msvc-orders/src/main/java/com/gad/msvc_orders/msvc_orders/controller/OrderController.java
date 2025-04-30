package com.gad.msvc_orders.msvc_orders.controller;

import com.gad.msvc_orders.msvc_orders.dto.DataResponse;
import com.gad.msvc_orders.msvc_orders.dto.OrderDTO;
import com.gad.msvc_orders.msvc_orders.service.OrderService;
import com.gad.msvc_orders.msvc_orders.utils.Enums;
import com.gad.msvc_orders.msvc_orders.utils.UtilsMethods;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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

@Slf4j
@RequestMapping("api/v1/orders")
@RestController
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final OrderService orderService;

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
