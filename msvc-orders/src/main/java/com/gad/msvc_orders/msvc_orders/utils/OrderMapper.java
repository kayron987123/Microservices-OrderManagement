package com.gad.msvc_orders.msvc_orders.utils;

import com.gad.msvc_orders.msvc_orders.dto.OrderDTO;
import com.gad.msvc_orders.msvc_orders.model.Order;

public class OrderMapper {
    private OrderMapper() {
    }

    public static OrderDTO toDTO(Order order) {
        return  new OrderDTO(order.getUuid().toString(),
                order.getUuidCustomer().toString(),
                UtilsMethods.dateTimeNowFormatted(),
                order.getStatus().name(),
                order.getTotalPrice());
    }
}
