package com.gad.msvc_products.controller;

import com.gad.msvc_products.assembler.ProductModelAssembler;
import com.gad.msvc_products.dto.ProductDTO;
import com.gad.msvc_products.dto.ProductPageDTO;
import com.gad.msvc_products.exception.GlobalExceptionHandler;
import com.gad.msvc_products.exception.ProductNotFoundException;
import com.gad.msvc_products.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler;

    @Mock
    private ProductModelAssembler productModelAssembler;

    @InjectMocks
    private ProductController productController;

    private UUID uuid;
    private ProductDTO productDTO;
    private ProductPageDTO productPageDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        uuid = UUID.randomUUID();
        productDTO = new ProductDTO(uuid, "Test Product", BigDecimal.valueOf(100), 10);
        List<ProductDTO> productDTOList = new ArrayList<>();
        productDTOList.add(productDTO);
        productDTOList.add(new ProductDTO(UUID.randomUUID(), "Another Product", BigDecimal.valueOf(120), 5));
        productDTOList.add(new ProductDTO(UUID.randomUUID(), "Third Product", BigDecimal.valueOf(150), 15));
        productDTOList.add(new ProductDTO(UUID.randomUUID(), "Fourth Product", BigDecimal.valueOf(180), 20));
        productDTOList.add(new ProductDTO(UUID.randomUUID(), "Fifth Product", BigDecimal.valueOf(220), 25));
        productDTOList.add(new ProductDTO(UUID.randomUUID(), "Sixth Product", BigDecimal.valueOf(2000), 30));


        productPageDTO = new ProductPageDTO(productDTOList, 0, 5, 1, 1);
    }

    @Test
    @DisplayName("Should return status 200 and Product when product UUID exists")
    void getProductByUuid_WhenUuidExists_ReturnsStatus200AndProduct() throws Exception {
        String uuidProduct = uuid.toString();

        when(productService.getProductByUuid(uuidProduct)).thenReturn(productDTO);

        mockMvc.perform(get("/api/v1/products/{uuid}", uuid))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Product found"))
                .andExpect(jsonPath("$.data.uuid_product").value(uuid.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Product"))
                .andExpect(jsonPath("$.data.price").value(100))
                .andExpect(jsonPath("$.data.stock").value(10))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(productService, times(1)).getProductByUuid(uuidProduct);
    }

    @Test
    @DisplayName("Should return status 404 and throw ProductNotFoundException when Uuid not exists")
    void getProductByUuid_WhenUuidNotExists_ReturnsStatus400AndThrowProductNotFoundException() throws Exception {
        String uuidString = uuid.toString();
        when(productService.getProductByUuid(uuidString)).thenThrow(new ProductNotFoundException("Product not found with uuid: " + uuid));

        mockMvc.perform(get("/api/v1/products/{uuid}", uuidString))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with uuid: " + uuid))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(productService, times(1)).getProductByUuid(uuidString);
    }

    @Test
    @DisplayName("Should return status 200 and paginated products when filters matches")
    void getAllProductsByNameAndPriceAndStock_WhenFiltersMatches_ReturnsStatus200AndPaginatedProducts() throws Exception {
        when(productService.getAllProductsByNameAndPriceAndStock(
                any(Pageable.class), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt()))
                .thenReturn(productPageDTO);

        List<EntityModel<ProductDTO>> content = productPageDTO.content().stream()
                .map(EntityModel::of)
                .toList();

        PagedModel<EntityModel<ProductDTO>> pagedModel = PagedModel.of(
                content,
                new PagedModel.PageMetadata(5, 0, productPageDTO.totalElements())
        );

        when(pagedResourcesAssembler.toModel(any(Page.class), any(ProductModelAssembler.class)))
                .thenReturn(pagedModel);

        mockMvc.perform(get("/api/v1/products")
                        .param("name", "Product")
                        .param("minPrice", "50")
                        .param("maxPrice", "150")
                        .param("stock", "5")
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Products found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(productService, times(1))
                .getAllProductsByNameAndPriceAndStock(
                        any(Pageable.class), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt());
    }

    @Test
    @DisplayName("Should return status 404 and throw ProductNotFoundException when name of product not exists")
    void getAllProductsByNameAndPriceAndStock_WhenNameNotExists_ReturnsStatus404AndThrowProductNotFoundException() throws Exception {
        String name = "Non Existent Product";
        when(productService.getAllProductsByNameAndPriceAndStock(
                any(), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt()))
                .thenThrow(new ProductNotFoundException("Product not found with word: " + name));

        mockMvc.perform(get("/api/v1/products")
                        .param("name", name)
                        .param("minPrice", "50")
                        .param("maxPrice", "150")
                        .param("stock", "5")
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with word: " + name))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(productService, times(1))
                .getAllProductsByNameAndPriceAndStock(any(), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt());
    }

    @Test
    @DisplayName("Should return status 404 and throw ProductNotFoundException when no products found with the given criteria")
    void getAllProductsByNameAndPriceAndStock_WhenNoProductsFound_ReturnsStatus404AndThrowProductNotFoundException() throws Exception {
        when(productService.getAllProductsByNameAndPriceAndStock(
                any(), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt()))
                .thenThrow(new ProductNotFoundException("No products found with the given criteria"));

        mockMvc.perform(get("/api/v1/products")
                        .param("name", "Non Existent Product")
                        .param("minPrice", "50")
                        .param("maxPrice", "150")
                        .param("stock", "5")
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No products found with the given criteria"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(productService, times(1))
                .getAllProductsByNameAndPriceAndStock(any(), anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt());
    }
}