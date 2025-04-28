package com.gad.msvc_details_order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
public class Product {
    @JsonProperty("uuid_product")
    private UUID uuidProduct;
    private String name;
    private BigDecimal price;
    private Integer stock;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(uuidProduct, product.uuidProduct) && Objects.equals(name, product.name) && Objects.equals(price, product.price) && Objects.equals(stock, product.stock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuidProduct, name, price, stock);
    }
}
