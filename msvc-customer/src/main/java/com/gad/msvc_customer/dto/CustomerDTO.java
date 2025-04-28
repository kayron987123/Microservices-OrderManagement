package com.gad.msvc_customer.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerDTO(
        String uuid,
        String name,
        @JsonProperty("last_name")
        String lastName
) {
}
