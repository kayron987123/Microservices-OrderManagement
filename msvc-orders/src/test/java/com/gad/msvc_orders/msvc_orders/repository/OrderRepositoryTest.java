package com.gad.msvc_orders.msvc_orders.repository;

import com.gad.msvc_orders.msvc_orders.enums.OrderStatusEnum;
import com.gad.msvc_orders.msvc_orders.model.Order;
import com.gad.msvc_orders.msvc_orders.utils.UtilsMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataMongoTest
class OrderRepositoryTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    @Autowired
    private OrderRepository orderRepository;
    private Order order;

    @Test
    void mongoDBContainerIsRunning() {
        assertTrue(mongoDBContainer.isCreated());
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        order = new Order();
        order.setUuid(UUID.randomUUID());
        order.setUuidCustomer(UUID.randomUUID());
        order.setOrderDate(UtilsMethods.dateTimeNowFormatted());
        order.setStatus(OrderStatusEnum.PENDING);
        order.setTotalPrice(new BigDecimal("100.00"));

        orderRepository.save(order);
    }

    @Test
    @DisplayName("Should return order when uuid exists")
    void findOrderByUuid_WhenUuidExists_ReturnOrder() {
        Order foundOrder = orderRepository.findOrderByUuid(order.getUuid()).orElse(null);

        assertNotNull(foundOrder);
        assertEquals(order.getUuid(), foundOrder.getUuid());
        assertEquals(order.getUuidCustomer(), foundOrder.getUuidCustomer());
        assertEquals(order.getStatus(), foundOrder.getStatus());
        assertEquals(order.getTotalPrice(), foundOrder.getTotalPrice());
        assertEquals(order.getOrderDate(), foundOrder.getOrderDate());
    }

    @Test
    @DisplayName("Should return null when uuid does not exist")
    void findOrderByUuid_WhenUuidDoesNotExist_ReturnNull() {
        UUID nonExistingUuid = UUID.randomUUID();
        Order foundOrder = orderRepository.findOrderByUuid(nonExistingUuid).orElse(null);

        assertNull(foundOrder);
    }
}