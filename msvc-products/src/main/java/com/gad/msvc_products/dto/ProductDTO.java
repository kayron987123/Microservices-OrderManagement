package com.gad.msvc_products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDTO(
        @JsonProperty("uuid_product")
        UUID uuidProduct,
        String name,
        BigDecimal price,
        Integer stock
) {
}
