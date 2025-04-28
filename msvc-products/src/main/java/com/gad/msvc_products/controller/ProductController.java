package com.gad.msvc_products.controller;

import com.gad.msvc_products.assembler.ProductModelAssembler;
import com.gad.msvc_products.dto.DataResponse;
import com.gad.msvc_products.dto.ProductDTO;
import com.gad.msvc_products.dto.ProductPageDTO;
import com.gad.msvc_products.service.ProductService;
import com.gad.msvc_products.utils.Enums;
import com.gad.msvc_products.utils.FormatterDateTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Filter Products", description = "Rest Controller for Products")
@Validated
@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler;
    private final ProductModelAssembler productModelAssembler;

    @Operation(
            summary = "Search Product by UUID",
            description = "Find Product by UUID",
            tags = {"Find Product"},
            parameters = {
                    @Parameter(name = "uuid", description = "UUID of the product to be searched", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid UUID format or uuid empty",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found with uuid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{uuid}")
    public ResponseEntity<DataResponse> getProductByUuid(@PathVariable @Pattern(regexp = Enums.PATTERN_REGEX, message = "Invalid UUID format")
                                                             @NotBlank(message = "Customer UUID cannot be empty") String uuid) {
        return ResponseEntity.ok().body(new DataResponse(
                OK.value(),
                "Product found",
                productService.getProductByUuid(uuid),
                FormatterDateTime.dateTimeNowFormatted(),
                null)
        );
    }

    @Operation(
            summary = "Search Products by filters",
            description = "Find Product by filters",
            tags = {"Find Products"},
            parameters = {
                    @Parameter(name = "page", description = "Page number (0 by default)", required = false),
                    @Parameter(name = "size", description = "Page size (5 by default)", required = false),
                    @Parameter(name = "name", description = "Name of the product to be searched", required = false),
                    @Parameter(name = "minPrice", description = "Minimum price of the product to be searched", required = false),
                    @Parameter(name = "maxPrice", description = "Maximum price of the product to be searched", required = false),
                    @Parameter(name = "stock", description = "Stock of the product to be searched", required = false)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Products found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation incorrect",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found with name or Products not found with filters",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataResponse.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<DataResponse> getAllProductsByNameAndPriceAndStock(@PageableDefault(size = 5, sort = {"name"}) Pageable pageable,
                                                                             @RequestParam(required = false) @Size(message = "The size minimum is 0") String name,
                                                                             @RequestParam(required = false) @Min(value = 1, message = "The minimum price must be greater than 0") BigDecimal minPrice,
                                                                             @RequestParam(required = false) @Max(value = 10000, message = "The maximum price must be less than 10000") BigDecimal maxPrice,
                                                                             @RequestParam(required = false) @Min(value = 0, message = "The stock cannot be negative") Integer stock) {

        ProductPageDTO productPageDTO = productService.getAllProductsByNameAndPriceAndStock(pageable.getPageNumber(), pageable.getPageSize(), name, minPrice, maxPrice, stock);

        return ResponseEntity.ok().body(new DataResponse(
                OK.value(),
                "Products found",
                pagedResourcesAssembler.toModel(
                        new PageImpl<>(productPageDTO.content(), pageable, productPageDTO.totalElements()),
                        productModelAssembler),
                FormatterDateTime.dateTimeNowFormatted(),
                null
        ));
    }
}
