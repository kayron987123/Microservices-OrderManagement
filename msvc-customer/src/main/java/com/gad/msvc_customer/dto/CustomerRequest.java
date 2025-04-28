package com.gad.msvc_customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @JsonProperty("name")
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
        String name,

        @JsonProperty("last_name")
        @NotBlank(message = "Last name cannot be empty")
        @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
        String lastName,

        @JsonProperty("password")
        @NotBlank(message = "Password cannot be empty")
        @Size(min = 5, max = 50, message = "Password must be between 5 and 50 characters")
        String password,

        @JsonProperty("email")
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email is not valid")
        String email,

        @JsonProperty("phone")
        @NotBlank(message = "Phone cannot be empty")
        @Pattern(regexp = "^9\\d{8}$", message = "Phone number must be exactly 9 digits and start with 9")
        String phone
) {
}
