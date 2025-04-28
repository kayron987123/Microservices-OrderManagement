package com.gad.msvc_details_order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gad.msvc_details_order.utils.Enums;
import jakarta.validation.constraints.*;


public record CreateOrderDetailRequest(
        @Pattern(regexp = Enums.PATTERN_REGEX, message = "Invalid UUID format")
        @NotBlank(message = "Order UUID cannot be empty")
        @JsonProperty("uuid_order")
        String uuidOrder,

        @Pattern(regexp = Enums.PATTERN_REGEX, message = "Invalid UUID format")
        @NotBlank(message = "Customer UUID cannot be empty")
        @JsonProperty("uuid_product")
        String uuidProduct,

        @NotNull(message = "Amount cannot be null")
        @Min(value = 1, message = "The amount must be greater than 0")
        @JsonProperty("amount")
        Integer amount
) {
}
