package com.gad.msvc_products.repository;

import com.gad.msvc_products.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findProductByUuid(UUID uuid);
    Page<Product> findProductsByNameContainingIgnoreCase(Pageable pageable, String name);

    @Query("""
            SELECT p FROM Product p
            WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:stock IS NULL OR p.stock >= :stock)
            """)
    Page<Product> findProductsByNameAndPriceAndStock(Pageable pageable,
                                                     @Param("name") String name,
                                                     @Param("minPrice") BigDecimal minPrice,
                                                     @Param("maxPrice") BigDecimal maxPrice,
                                                     @Param("stock") Integer stock);
}
