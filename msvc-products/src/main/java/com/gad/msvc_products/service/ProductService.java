package com.gad.msvc_products.service;

import com.gad.msvc_products.dto.ProductDTO;
import com.gad.msvc_products.dto.ProductPageDTO;
import com.gad.msvc_products.exception.ProductNotFoundException;
import com.gad.msvc_products.model.Product;
import com.gad.msvc_products.repository.ProductRepository;
import com.gad.msvc_products.utils.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Cacheable(value = "productsByUuid", key = "#uuid")
    @Transactional(readOnly = true)
    public ProductDTO getProductByUuid(String uuid) {
        UUID uuidProduct = UUID.fromString(uuid);
        Product product = productRepository.findProductByUuid(uuidProduct).orElseThrow(() -> new ProductNotFoundException("Product not found with uuid: " + uuidProduct));
        return ProductMapper.toDTO(product);
    }

    @Cacheable(value = "listProductsByNameAndPriceAndStock",
            key = "'page:' + #page + '-size:' + #size + '-name:' + #name + '-min:' + #minPrice + '-max:' + #maxPrice + '-stock:' + #stock")
    @Transactional(readOnly = true)
    public ProductPageDTO getAllProductsByNameAndPriceAndStock(int page, int size, String name, BigDecimal minPrice, BigDecimal maxPrice, Integer stock) {
        if (name != null && !name.isEmpty() && productRepository.findProductsByNameContainingIgnoreCase(PageRequest.of(page, size), name).isEmpty()) {
            throw new ProductNotFoundException("Product not found with word: " + name);
        }

        Page<ProductDTO> products = productRepository.findProductsByNameAndPriceAndStock(PageRequest.of(page, size), name, minPrice, maxPrice, stock)
                .map(ProductMapper::toDTO);

        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found with the given criteria");
        }

        return new ProductPageDTO(products.getContent(), products.getNumber(), products.getSize(), products.getTotalElements(), products.getTotalPages());
    }
}
