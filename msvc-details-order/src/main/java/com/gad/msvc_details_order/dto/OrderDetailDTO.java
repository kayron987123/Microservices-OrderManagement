package com.gad.msvc_details_order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record OrderDetailDTO(
        @JsonProperty("uuid_detail")
        String uuidDetail,
        @JsonProperty("uuid_order")
        String uuidOrder,
        @JsonProperty("product_name")
        String productName,
        Integer amount,
        @JsonProperty("unit_price")
        BigDecimal unitPrice
) {
}
