package com.gad.msvc_products.utils;

import com.gad.msvc_products.dto.ProductDTO;
import com.gad.msvc_products.model.Product;
import jakarta.annotation.Nullable;

public class ProductMapper {
    private ProductMapper() {
    }

    @Nullable
    public static ProductDTO toDTO(@Nullable final Product product) {
        if(product == null) return null;
        return new ProductDTO(product.getUuid(), product.getName(), product.getPrice(), product.getStock());
    }
}
