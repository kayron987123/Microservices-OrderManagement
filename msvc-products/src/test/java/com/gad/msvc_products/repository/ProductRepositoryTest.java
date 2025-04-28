package com.gad.msvc_products.repository;

import com.gad.msvc_products.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @Autowired
    private ProductRepository productRepository;
    private Product product1;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        product1 = new Product();
        product1.setUuid(UUID.randomUUID());
        product1.setName("Product 1 test");
        product1.setPrice(BigDecimal.valueOf(50));
        product1.setStock(20);


        Product product2 = new Product();
        product2.setUuid(UUID.randomUUID());
        product2.setName("Product 2 test");
        product2.setPrice(BigDecimal.valueOf(100));
        product2.setStock(10);

        productRepository.saveAll(List.of(product1, product2));
    }

    @Test
    @DisplayName("Should return product when UUID exists")
    void findProductByUuid_WhenUuidExists_ReturnsProduct() {
        Optional<Product> productFound = productRepository.findProductByUuid(product1.getUuid());

        assertTrue(productFound.isPresent());
        assertEquals(product1.getUuid(), productFound.get().getUuid());
    }

    @Test
    @DisplayName("Should return empty optional when UUID does not exist")
    void findProductByUuid_WhenUuidDoesNotExist_ReturnsEmptyOptional() {
        Optional<Product> productFound = productRepository.findProductByUuid(UUID.randomUUID());

        assertTrue(productFound.isEmpty());
    }

    @Test
    @DisplayName("Should return products when name contains search term")
    void findProductsByNameContainingIgnoreCase_WhenNameContainsTerm_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameContainingIgnoreCase(
                PageRequest.of(0, 10), "pro");

        assertEquals(2, productsFound.getTotalElements());
        assertEquals("Product 1 test", productsFound.getContent().getFirst().getName());
    }

    @Test
    @DisplayName("Should return empty page when name does not contain search term")
    void findProductsByNameContainingIgnoreCase_WhenNameDoesNotContainTerm_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameContainingIgnoreCase(
                PageRequest.of(0, 10), "non-existent");

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering by all parameters")
    void findProductsByNameAndPriceAndStock_WhenFilteringByAllParameters_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                BigDecimal.valueOf(49),
                BigDecimal.valueOf(101),
                10);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return all products when all parameters are null")
    void findProductsByNameAndPriceAndStock_WhenAllParametersNull_ReturnsAllProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                null,
                null);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering only by name")
    void findProductsByNameAndPriceAndStock_WhenFilteringByNameOnly_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                null,
                null);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when name does not exist")
    void findProductsByNameAndPriceAndStock_WhenNameDoesNotExist_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                null,
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering only by minimum price")
    void findProductsByNameAndPriceAndStock_WhenFilteringByMinPriceOnly_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(50),
                null,
                null);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when minimum price is higher than product prices")
    void findProductsByNameAndPriceAndStock_WhenMinPriceHigherThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(150),
                null,
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering only by maximum price")
    void findProductsByNameAndPriceAndStock_WhenFilteringByMaxPriceOnly_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                BigDecimal.valueOf(101),
                null);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when maximum price is lower than product prices")
    void findProductsByNameAndPriceAndStock_WhenMaxPriceLowerThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                BigDecimal.valueOf(49),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering only by stock")
    void findProductsByNameAndPriceAndStock_WhenFilteringByStockOnly_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                null,
                10);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when stock is greater than all products")
    void findProductsByNameAndPriceAndStock_WhenStockGreaterThanAllProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                null,
                500);

        assertEquals(0, productsFound.getTotalElements());
    }

    //aa
    @Test
    @DisplayName("Should return products when filtering by name and minimum price")
    void findProductsByNameAndPriceAndStock_WhenFilteringByNameAndMinPrice_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                BigDecimal.valueOf(49),
                null,
                null);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by non-existent name and minimum price")
    void findProductsByNameAndPriceAndStock_WhenNonExistentNameAndMinPrice_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                BigDecimal.valueOf(49),
                null,
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name and minimum price higher than products")
    void findProductsByNameAndPriceAndStock_WhenNameAndMinPriceHigherThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                BigDecimal.valueOf(200),
                null,
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing and minimum price is higher than product price")
    void findProductsByNameAndPriceAndStock_WhenNameNotExistingAndMinPriceHigherThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                BigDecimal.valueOf(200),
                null,
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering by name and maximum price")
    void findProductsByNameAndPriceAndStock_WhenNameAndMaxPrice_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                BigDecimal.valueOf(101),
                null);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing and maximum price")
    void findProductsByNameAndPriceAndStock_WhenNameNotExistingAndMaxPrice_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                BigDecimal.valueOf(101),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name and maximum price is lower than product price")
    void findProductsByNameAndPriceAndStock_WhenNameAndMaxPriceLowerThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                BigDecimal.valueOf(49),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing and maximum price is lower than product price")
    void findProductsByNameAndPriceAndStock_WhenNameNotExistingAndMaxPriceLowerThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                BigDecimal.valueOf(49),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering by name and stock")
    void findProductsByNameAndPriceAndStock_WhenNameAndStock_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                null,
                10);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing and stock")
    void findProductsByNameAndPriceAndStock_WhenNameNotExistingAndStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                null,
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name and stock greater than all products")
    void findProductsByNameAndPriceAndStock_WhenNameAndStockGreaterThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                null,
                200);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing and stock greater than all products")
    void findProductsByNameAndPriceAndStock_WhenNameNotExistingAndStockGreaterThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                null,
                200);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering by minimum price and maximum price")
    void findProductsByNameAndPriceAndStock_WhenMinPriceAndMaxPrice_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(49),
                BigDecimal.valueOf(101),
                null);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price greater than price of products and maximum price")
    void findProductsByNameAndPriceAndStock_WhenMinPriceGreaterThanProductsAndMaxPrice_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(200),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price and maximum price lower than price of products")
    void findProductsByNameAndPriceAndStock_WhenMinPriceAndMaxPriceLowerThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                BigDecimal.valueOf(48),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price greater than price of products and maximum price lower than price of products")
    void findProductsByNameAndPriceAndStock_WhenMinPriceGreaterAndMaxPriceLowerThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(49),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering by minimum price and stock")
    void findProductsByNameAndPriceAndStock_WhenMinPriceAndStock_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(49),
                null,
                10);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price greater than price of products and stock")
    void findProductsByNameAndPriceAndStock_WhenMinPriceGreaterThanProductsAndStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(150),
                null,
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price and stock greater than stock of products")
    void findProductsByNameAndPriceAndStock_WhenMinPriceAndStockGreaterThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(49),
                null,
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price greater than price of products and stock greater than stock of products")
    void findProductsByNameAndPriceAndStock_WhenMinPriceGreaterThanProductsAndStockGreaterThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(150),
                null,
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering by maximum price and stock")
    void findProductsByNameAndPriceAndStock_WhenMaxPriceAndStock_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                BigDecimal.valueOf(101),
                10);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by maximum price lower than price of products and stock")
    void findProductsByNameAndPriceAndStock_WhenMaxPriceLowerThanProductsAndStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                BigDecimal.valueOf(49),
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by maximum price and stock greater than stock of products")
    void findProductsByNameAndPriceAndStock_WhenMaxPriceAndStockGreaterThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                BigDecimal.valueOf(101),
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by maximum price lower than price of products and stock greater than stock of products")
    void findProductsByNameAndPriceAndStock_WhenMaxPriceLowerThanProductsAndStockGreaterThanProducts_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                BigDecimal.valueOf(49),
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products filtering by name, minimum price and maximum price")
    void findProductsByNameMinimumPriceAndMaximumPrice_WhenNameAndMinPriceAndMaxPriceProvided_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                BigDecimal.valueOf(49),
                BigDecimal.valueOf(101),
                null);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, minimum price and maximum price")
    void findProductsByNameNotExistingMinimumPriceAndMaximumPrice_WhenNonExistentNameAndMinPriceAndMaxPriceProvided_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                BigDecimal.valueOf(49),
                BigDecimal.valueOf(101),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name, minimum price greater than price of products and maximum price")
    void findProductsByNameMinimumPriceGreaterThanPriceOfProductsAndMaximumPrice_WhenMinPriceTooHighAndMaxPriceProvided_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(200),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name, minimum price and maximum price lower than price of products")
    void findProductsByNameMinimumPriceAndMaximumPriceLowerThanPriceOfProducts_WhenMaxPriceTooLowAndNameProvided_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                BigDecimal.valueOf(48),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, minimum price greater than price of products and maximum price")
    void findProductsByNameNotExistingMinimumPriceGreaterThanPriceOfProductsAndMaximumPrice_WhenNonExistentNameAndMinPriceTooHigh_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(49),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, minimum price and maximum price lower than price of products")
    void findProductsByNameNotExistingMinimumPriceAndMaximumPriceLowerThanPriceOfProducts_WhenNonExistentNameAndMaxPriceTooLow_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                BigDecimal.valueOf(48),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name, minimum price greater than price of products and maximum price lower than price of products")
    void findProductsByNameMinimumPriceGreaterThanPriceOfProductsAndMaximumPriceLowerThanPriceOfProducts_WhenMinPriceTooHighAndMaxPriceTooLow_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(48),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, minimum price greater than price of products and maximum price lower than price of products")
    void findProductsByNameNotExistingMinimumPriceGreaterThanPriceOfProductsAndMaximumPriceLowerThanPriceOfProducts_WhenNonExistentNameMinPriceTooHighAndMaxPriceTooLow_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(48),
                null);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products filtering by name, minimum price and stock")
    void findProductsByNameMinimumPriceAndStock_WhenNameMinPriceAndStockProvided_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                BigDecimal.valueOf(49),
                null,
                10);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, minimum price and stock")
    void findProductsByNameNotExistingMinimumPriceAndStock_WhenNonExistentNameMinPriceAndStockProvided_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                BigDecimal.valueOf(49),
                null,
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name, minimum price greater than price of products and stock")
    void findProductsByNameMinimumPriceGreaterThanPriceOfProductsAndStock_WhenMinPriceTooHighAndStockProvided_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                BigDecimal.valueOf(150),
                null,
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name, minimum price and stock greater than stock of products")
    void findProductsByNameAndMinimumPriceAndStockGreaterThanStock_WhenStockTooHighWithNameMinPriceProvided_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                null,
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, minimum price greater than price of products and stock")
    void findProductsByNameNotExistingAndMinimumPriceGreaterThanPriceOfProductsAndStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                BigDecimal.valueOf(150),
                null,
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, minimum price and stock greater than stock of products")
    void findProductsByNameNotExistingAndMinimumPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                null,
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name, minimum price greater than price of products and stock greater than stock of products")
    void findProductsByNameAndMinimumPriceGreaterThanPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                BigDecimal.valueOf(150),
                null,
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, minimum price greater than price of products and stock greater than stock of products")
    void findProductsByNameNotExistingAndMinimumPriceGreaterThanPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                BigDecimal.valueOf(150),
                null,
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering by name, maximum price and stock")
    void findProductsByNameAndMaximumPriceAndStock_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                BigDecimal.valueOf(101),
                10);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, maximum price and stock")
    void findProductsByNameNotExistingAndMaximumPriceAndStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                BigDecimal.valueOf(101),
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name, maximum price lower than price of products and stock")
    void findProductsByNameAndMaximumPriceLowerThanPriceAndStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                BigDecimal.valueOf(49),
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name, maximum price and stock greater than stock of products")
    void findProductsByNameAndMaximumPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                BigDecimal.valueOf(101),
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, maximum price lower than price of products and stock")
    void findProductsByNameNotExistingAndMaximumPriceLowerThanPriceAndStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                BigDecimal.valueOf(49),
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, maximum price and stock greater than stock of products")
    void findProductsByNameNotExistingAndMaximumPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                BigDecimal.valueOf(101),
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name, maximum price lower than price of products and stock greater than stock of products")
    void findProductsByNameAndMaximumPriceLowerThanPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "Product",
                null,
                BigDecimal.valueOf(49),
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by name not existing, maximum price lower than price of products and stock greater than stock of products")
    void findProductsByNameNotExistingAndMaximumPriceLowerThanPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                "non-existent",
                null,
                BigDecimal.valueOf(49),
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return products when filtering by minimum price, maximum price and stock")
    void findProductsByMinimumPriceAndMaximumPriceAndStock_ReturnsProducts() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(49),
                BigDecimal.valueOf(101),
                10);

        assertEquals(2, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price greater than price of products, maximum price and stock")
    void findProductsByMinimumPriceGreaterThanPriceAndMaximumPriceAndStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(200),
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price, maximum price lower than price of products and stock")
    void findProductsByMinimumPriceAndMaximumPriceLowerThanPriceAndStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                BigDecimal.valueOf(48),
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price, maximum price and stock greater than stock of products")
    void findProductsByMinimumPriceAndMaximumPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(49),
                BigDecimal.valueOf(101),
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price greater than price of products, maximum price lower than price of products and stock")
    void findProductsByMinimumPriceGreaterThanPriceAndMaximumPriceLowerThanPriceAndStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(48),
                10);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price greater than price of products, maximum price and stock greater than stock of products")
    void findProductsByMinimumPriceGreaterThanPriceAndMaximumPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(200),
                null,
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price, maximum price lower than price of products and stock greater than stock of products")
    void findProductsByMinimumPriceAndMaximumPriceLowerThanPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                null,
                BigDecimal.valueOf(48),
                30);

        assertEquals(0, productsFound.getTotalElements());
    }

    @Test
    @DisplayName("Should return empty page when filtering by minimum price greater than price of products, maximum price lower than price of products and stock greater than stock of products")
    void findProductsByMinimumPriceGreaterThanPriceAndMaximumPriceLowerThanPriceAndStockGreaterThanStock_ReturnsEmptyPage() {
        Page<Product> productsFound = productRepository.findProductsByNameAndPriceAndStock(
                PageRequest.of(0, 10),
                null,
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(48),
                30);

        assertEquals(0, productsFound.getTotalElements());
    }
}