package com.gad.msvc_details_order.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "order_details")
public class OrderDetail {
    @Id
    @Field("uuid_detail")
    private UUID uuidDetail = UUID.randomUUID();

    @Field("uuid_order")
    private UUID uuidOrder;

    @Field("uuid_product")
    private UUID uuidProduct;

    private Integer amount;

    @Field("unit_price")
    private BigDecimal unitPrice;
}
