package com.gad.msvc_orders.msvc_orders.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDetail {
    private Integer amount;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @Override
    public int hashCode() {
        return Objects.hash(amount, unitPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetail that = (OrderDetail) o;
        return Objects.equals(amount, that.amount) &&
                Objects.equals(unitPrice, that.unitPrice);
    }
}
