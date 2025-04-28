package com.gad.msvc_orders.msvc_orders.service;

import com.gad.msvc_orders.msvc_orders.dto.OrderDTO;
import com.gad.msvc_orders.msvc_orders.enums.OrderStatusEnum;
import com.gad.msvc_orders.msvc_orders.exception.OrderNotFoundException;
import com.gad.msvc_orders.msvc_orders.model.Order;
import com.gad.msvc_orders.msvc_orders.model.OrderDetail;
import com.gad.msvc_orders.msvc_orders.repository.OrderRepository;
import com.gad.msvc_orders.msvc_orders.service.feign.OrderDetailServiceFeign;
import com.gad.msvc_orders.msvc_orders.utils.OrderMapper;
import com.gad.msvc_orders.msvc_orders.utils.UtilsMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final JwtService jwtService;
    private final OrderDetailServiceFeign orderDetailServiceFeign;
    private static final String ORDER_UUID_TEXT = "Order with uuid ";
    private static final String NOT_FOUND_TEXT = " not found";

    @Transactional
    public OrderDTO createOrder(String token) {
        UUID uuidCustomer = jwtService.getUserUuidFromJwt(token);
        Order order = new Order();
        order.setOrderDate(UtilsMethods.dateTimeNowFormatted());
        order.setStatus(OrderStatusEnum.PENDING);
        order.setUuidCustomer(uuidCustomer);
        order.setTotalPrice(BigDecimal.ZERO);

        return OrderMapper.toDTO(orderRepository.save(order));
    }

    @Cacheable(value = "OrderByUuid", key = "#uuidOrder")
    @Transactional(readOnly = true)
    public OrderDTO findByUuid(String uuidOrder) {
        UUID uuidOrderRequest = UUID.fromString(uuidOrder);
        Order order = orderRepository.findOrderByUuid(uuidOrderRequest).orElseThrow(() -> new OrderNotFoundException(ORDER_UUID_TEXT + uuidOrder + NOT_FOUND_TEXT));

        return OrderMapper.toDTO(order);
    }

    @CacheEvict(value = "OrderByUuid", key = "#uuidOrder")
    @Transactional
    public OrderDTO updateTotalPrice(String uuidOrderDetail, String uuidOrder) {
        OrderDetail orderDetail = orderDetailServiceFeign.findOrderDetailByUuid(uuidOrderDetail);
        Order orderDb = orderRepository.findOrderByUuid(UUID.fromString(uuidOrder)).orElseThrow(() -> new OrderNotFoundException(ORDER_UUID_TEXT + uuidOrder + NOT_FOUND_TEXT));

        orderDb.setTotalPrice(calculateTotalPrice(orderDetail.getAmount(), orderDetail.getUnitPrice()));
        orderDb.setStatus(OrderStatusEnum.DELIVERED);
        return OrderMapper.toDTO(orderRepository.save(orderDb));
    }

    @Transactional
    public void deleteOrder(String uuidOrder) {
        UUID uuidOrderRequest = UUID.fromString(uuidOrder);
        Order order = orderRepository.findOrderByUuid(uuidOrderRequest).orElseThrow(() -> new OrderNotFoundException(ORDER_UUID_TEXT + uuidOrder + NOT_FOUND_TEXT));
        order.setStatus(OrderStatusEnum.CANCELED);
        orderRepository.save(order);
    }

    private BigDecimal calculateTotalPrice(Integer amount, BigDecimal unitPrice) {
        return unitPrice.multiply(BigDecimal.valueOf(amount));
    }
}
