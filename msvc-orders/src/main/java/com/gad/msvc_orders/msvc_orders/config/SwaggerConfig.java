package com.gad.msvc_orders.msvc_orders.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        info = @Info(
                title = "Orders API",
                description = "API for managing orders",
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
@SecurityScheme(
        name = "Bearer Authentication",
        description = "Access token for authorization",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}
