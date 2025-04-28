package com.gad.msvc_orders.msvc_orders.model;

import com.gad.msvc_orders.msvc_orders.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    @Field("uuid_order")
    private UUID uuid = UUID.randomUUID();

    @Field("uuid_customer")
    private UUID uuidCustomer;

    @Field("order_date")
    private String orderDate;

    private OrderStatusEnum status;

    @Field("total_price")
    private BigDecimal totalPrice;
}
