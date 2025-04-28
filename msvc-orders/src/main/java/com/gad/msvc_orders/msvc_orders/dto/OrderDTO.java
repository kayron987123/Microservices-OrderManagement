package com.gad.msvc_orders.msvc_orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record OrderDTO(
        @JsonProperty("uuid_order")
        String uuidOrder,
        @JsonProperty("uuid_customer")
        String uuidCustomer,
        @JsonProperty("order_date")
        String orderDate,
        @JsonProperty("status_order")
        String statusOrder,
        @JsonProperty("total_price")
        BigDecimal totalPrice
        ) {
}
