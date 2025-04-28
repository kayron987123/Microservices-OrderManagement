package com.gad.msvc_products.dto;

import java.util.List;

public record ProductPageDTO(
        List<ProductDTO> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
}
