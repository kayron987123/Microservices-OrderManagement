package com.gad.msvc_products.service;

import com.gad.msvc_products.dto.ProductDTO;
import com.gad.msvc_products.dto.ProductPageDTO;
import com.gad.msvc_products.exception.ProductNotFoundException;
import com.gad.msvc_products.model.Product;
import com.gad.msvc_products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private UUID uuid;

    @BeforeEach
    void serUp() {
        uuid = UUID.randomUUID();
        product = new Product();
        product.setId(1L);
        product.setUuid(uuid);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(10);
    }

    @Test
    @DisplayName("Should return product when UUID exists")
    void getProductByUuid_WhenUuidExists_ReturnsProduct() {
        when(productRepository.findProductByUuid(uuid)).thenReturn(Optional.of(product));
        ProductDTO result = productService.getProductByUuid(uuid.toString());

        assertNotNull(result);
        assertEquals(uuid, result.uuidProduct());
        assertEquals("Test Product", result.name());
        verify(productRepository, times(1)).findProductByUuid(uuid);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when UUID does not exist")
    void getProductByUuid_WhenUuidNotExists_ThrowProductNotFoundException() {
        String uuidString = uuid.toString();
        when(productRepository.findProductByUuid(uuid)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> productService.getProductByUuid(uuidString));
        assertEquals("Product not found with uuid: " + uuid, exception.getMessage());
        verify(productRepository, times(1)).findProductByUuid(uuid);
    }

    @Test
    @DisplayName("Should return product page when using filter")
    void getAllProductsByNameAndPriceAndStock_WhenProductsExists_ReturnProducts() {
        List<Product> productList = Collections.singletonList(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findProductsByNameContainingIgnoreCase(
                any(PageRequest.class),
                anyString()
        )).thenReturn(productPage);

        when(productRepository.findProductsByNameAndPriceAndStock(
                any(PageRequest.class),
                anyString(),
                any(BigDecimal.class),
                any(BigDecimal.class),
                anyInt()
        )).thenReturn(productPage);

        int page = 0;
        int size = 5;
        String name = "Bad";
        BigDecimal minPrice = BigDecimal.valueOf(50);
        BigDecimal maxPrice = BigDecimal.valueOf(150);
        int stock = 5;


        ProductPageDTO result = productService.getAllProductsByNameAndPriceAndStock(page, size, name, minPrice, maxPrice, stock);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(uuid, result.content().getFirst().uuidProduct());
        assertEquals(1, result.totalElements());
        verify(productRepository, times(1)).findProductsByNameAndPriceAndStock(
                any(PageRequest.class),
                eq(name),
                eq(minPrice),
                eq(maxPrice),
                eq(stock)
        );
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when no products match criteria")
    void getAllProductsByNameAndPriceAndStock_WhenNoProductsMatchFilter_ThrowProductNotFoundException() {
        List<Product> productList = Collections.singletonList(product);
        Page<Product> productPage = new PageImpl<>(productList);
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());

        when(productRepository.findProductsByNameContainingIgnoreCase(
                any(PageRequest.class),
                anyString()
        )).thenReturn(productPage);

        when(productRepository.findProductsByNameAndPriceAndStock(
                any(PageRequest.class),
                anyString(),
                any(BigDecimal.class),
                any(BigDecimal.class),
                anyInt()
        )).thenReturn(emptyPage);

        int page = 0;
        int size = 5;
        String name = "Test";
        BigDecimal minPrice = BigDecimal.valueOf(50);
        BigDecimal maxPrice = BigDecimal.valueOf(150);
        int stock = 11;

        Exception exception = assertThrows(ProductNotFoundException.class, () ->
                productService.getAllProductsByNameAndPriceAndStock(page, size, name, minPrice, maxPrice, stock));

        assertEquals("No products found with the given criteria", exception.getMessage());
        verify(productRepository, times(1)).findProductsByNameAndPriceAndStock(
                any(PageRequest.class),
                eq(name),
                eq(minPrice),
                eq(maxPrice),
                eq(stock)
        );
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when name search returns empty result")
    void getAllProductsByNameAndPriceAndStock_WhenNameSearchReturnsEmpty_ThrowProductNotFoundException() {
        when(productRepository.findProductsByNameContainingIgnoreCase(
                any(PageRequest.class),
                anyString()
        )).thenReturn(new PageImpl<>(Collections.emptyList()));

        int page = 0;
        int size = 5;
        String name = "Bad";
        BigDecimal minPrice = BigDecimal.valueOf(50);
        BigDecimal maxPrice = BigDecimal.valueOf(101);
        int stock = 5;

        Exception exception = assertThrows(ProductNotFoundException.class, () ->
                productService.getAllProductsByNameAndPriceAndStock(page, size, name, minPrice, maxPrice, stock));

        assertEquals("Product not found with word: " + name, exception.getMessage());
        verify(productRepository, times(1)).findProductsByNameContainingIgnoreCase(
                any(PageRequest.class),
                eq(name)
        );
        verify(productRepository, never()).findProductsByNameAndPriceAndStock(any(), any(), any(), any(), anyInt());
    }
}