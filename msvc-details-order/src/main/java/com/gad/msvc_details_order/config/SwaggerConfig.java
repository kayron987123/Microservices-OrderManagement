package com.gad.msvc_details_order.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@OpenAPIDefinition(
        info = @Info(
                title = "Order Details API",
                description = "API for managing order details",
                termsOfService = "https://example.com/terms",
                version = "1.0.0",
                contact = @Contact(
                        name = "Gad",
                        url = "https://example.com/contact",
                        email = "example@gmail.com"
                ),
                license = @License(
                        name = "Standard Software Use License for Products API",
                        url = "https://example.com/license"
                )
        ),
        security = @SecurityRequirement(
                name = "Bearer Authentication"
        )
)
public class SwaggerConfig {
}
