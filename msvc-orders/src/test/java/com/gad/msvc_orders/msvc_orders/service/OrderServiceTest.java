package com.gad.msvc_orders.msvc_orders.service;

import com.gad.msvc_orders.msvc_orders.dto.OrderDTO;
import com.gad.msvc_orders.msvc_orders.enums.OrderStatusEnum;
import com.gad.msvc_orders.msvc_orders.exception.OrderNotFoundException;
import com.gad.msvc_orders.msvc_orders.model.Order;
import com.gad.msvc_orders.msvc_orders.model.OrderDetail;
import com.gad.msvc_orders.msvc_orders.repository.OrderRepository;
import com.gad.msvc_orders.msvc_orders.service.feign.OrderDetailServiceFeign;
import com.gad.msvc_orders.msvc_orders.utils.UtilsMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private OrderDetailServiceFeign orderDetailServiceFeign;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDetail orderDetail;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setUuid(UUID.randomUUID());
        order.setOrderDate(UtilsMethods.dateTimeNowFormatted());
        order.setStatus(OrderStatusEnum.PENDING);
        order.setUuidCustomer(UUID.randomUUID());
        order.setTotalPrice(BigDecimal.valueOf(200));

        orderDetail = new OrderDetail();
        orderDetail.setAmount(10);
        orderDetail.setUnitPrice(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Should save order and return orderDTO when token is sent")
    void createOrder_WhenTokenSent_SaveOrderAndReturnsOrderDTO() {
        String token = "bearer token";
        when(jwtService.getUserUuidFromJwt(any(String.class))).thenReturn(order.getUuidCustomer());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO orderDTO = orderService.createOrder(token);

        assertNotNull(orderDTO);
        assertEquals(order.getUuid(), UUID.fromString(orderDTO.uuidOrder()));
        assertEquals(order.getOrderDate(), orderDTO.orderDate());
        assertEquals(order.getStatus(), OrderStatusEnum.valueOf(orderDTO.statusOrder()));
        assertEquals(order.getUuidCustomer(), UUID.fromString(orderDTO.uuidCustomer()));
        assertEquals(order.getTotalPrice(), orderDTO.totalPrice());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should return optional of order when uuid exists")
    void findByUuid() {
        UUID uuidOrder = order.getUuid();
        when(orderRepository.findOrderByUuid(uuidOrder)).thenReturn(java.util.Optional.of(order));

        OrderDTO orderDTO = orderService.findByUuid(uuidOrder.toString());

        assertNotNull(orderDTO);
        assertEquals(order.getUuid(), UUID.fromString(orderDTO.uuidOrder()));
        assertEquals(order.getOrderDate(), orderDTO.orderDate());
        assertEquals(order.getStatus(), OrderStatusEnum.valueOf(orderDTO.statusOrder()));
        assertEquals(order.getUuidCustomer(), UUID.fromString(orderDTO.uuidCustomer()));
        assertEquals(order.getTotalPrice(), orderDTO.totalPrice());
        verify(orderRepository, times(1)).findOrderByUuid(uuidOrder);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when uuid does not exist")
    void findByUuid_WhenUuidDoesNotExist_ThrowsOrderNotFoundException() {
        UUID uuidOrder = UUID.randomUUID();
        String uuidOrderString = uuidOrder.toString();
        when(orderRepository.findOrderByUuid(uuidOrder)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(OrderNotFoundException.class, () -> orderService.findByUuid(uuidOrderString));

        assertEquals("Order with uuid " + uuidOrder + " not found", exception.getMessage());
        verify(orderRepository, times(1)).findOrderByUuid(uuidOrder);
        verify(orderRepository, times(0)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should return orderDTO when uuid order detail and uuid order exists")
    void updateTotalPrice() {
        String orderDetailUuid = UUID.randomUUID().toString();

        when(orderDetailServiceFeign.findOrderDetailByUuid(any(String.class))).thenReturn(orderDetail);
        when(orderRepository.findOrderByUuid(any(UUID.class))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO orderDTO = orderService.updateTotalPrice(orderDetailUuid, order.getUuid().toString());

        assertNotNull(orderDTO);
        assertEquals(order.getUuid(), UUID.fromString(orderDTO.uuidOrder()));
        assertEquals(order.getOrderDate(), orderDTO.orderDate());
        assertEquals(order.getStatus(), OrderStatusEnum.valueOf(orderDTO.statusOrder()));
        assertEquals(order.getUuidCustomer(), UUID.fromString(orderDTO.uuidCustomer()));
        assertEquals(order.getTotalPrice(), orderDTO.totalPrice());
        verify(orderDetailServiceFeign, times(1)).findOrderDetailByUuid(orderDetailUuid);
        verify(orderRepository, times(1)).findOrderByUuid(any(UUID.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when uuid order does not exist")
    void updateTotalPrice_WhenUuidOrderDoesNotExist_ThrowsOrderNotFoundException() {
        String orderDetailUuid = UUID.randomUUID().toString();
        String orderUuid = order.getUuid().toString();
        when(orderDetailServiceFeign.findOrderDetailByUuid(any(String.class))).thenReturn(orderDetail);
        when(orderRepository.findOrderByUuid(any(UUID.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderNotFoundException.class, () -> orderService.updateTotalPrice(orderDetailUuid, orderUuid));

        assertEquals("Order with uuid " + order.getUuid() + " not found", exception.getMessage());
        verify(orderDetailServiceFeign, times(1)).findOrderDetailByUuid(orderDetailUuid);
        verify(orderRepository, times(1)).findOrderByUuid(any(UUID.class));
        verify(orderRepository, times(0)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should delete order when uuid order exists")
    void deleteOrder_WhenUuidOrder_ReturnsNothing() {
        when(orderRepository.findOrderByUuid(any(UUID.class))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.deleteOrder(order.getUuid().toString());

        verify(orderRepository, times(1)).findOrderByUuid(any(UUID.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when uuid order does not exist")
    void deleteOrder_WhenUuidOrderDoesNotExist_ThrowsOrderNotFoundException() {
        String uuidOrder = order.getUuid().toString();
        when(orderRepository.findOrderByUuid(any(UUID.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(uuidOrder));

        assertEquals("Order with uuid " + order.getUuid() + " not found", exception.getMessage());
        verify(orderRepository, times(1)).findOrderByUuid(any(UUID.class));
        verify(orderRepository, times(0)).save(any(Order.class));
    }
}