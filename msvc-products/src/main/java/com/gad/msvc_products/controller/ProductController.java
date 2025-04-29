package com.gad.msvc_products.controller;

import com.gad.msvc_products.assembler.ProductModelAssembler;
import com.gad.msvc_products.dto.DataResponse;
import com.gad.msvc_products.dto.ProductDTO;
import com.gad.msvc_products.dto.ProductPageDTO;
import com.gad.msvc_products.service.ProductService;
import com.gad.msvc_products.utils.Enums;
import com.gad.msvc_products.utils.FormatterDateTime;
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

@Validated
@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler;
    private final ProductModelAssembler productModelAssembler;

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

    @GetMapping
    public ResponseEntity<DataResponse> getAllProductsByNameAndPriceAndStock(@PageableDefault(size = 5, sort = {"name"}) Pageable pageable,
                                                                             @RequestParam(required = false) @Size(message = "The size minimum is 0") String name,
                                                                             @RequestParam(required = false) @Min(value = 1, message = "The minimum price must be greater than 0") BigDecimal minPrice,
                                                                             @RequestParam(required = false) @Max(value = 10000, message = "The maximum price must be less than 10000") BigDecimal maxPrice,
                                                                             @RequestParam(required = false) @Min(value = 0, message = "The stock cannot be negative") Integer stock) {

        ProductPageDTO productPageDTO = productService.getAllProductsByNameAndPriceAndStock(pageable, name, minPrice, maxPrice, stock);

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
