package com.gad.msvc_details_order.repository;

import com.gad.msvc_details_order.model.OrderDetail;
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
class OrderDetailRepositoryTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    private OrderDetail orderDetail;

    @BeforeEach
    void setUp() {
        orderDetailRepository.deleteAll();

        orderDetail = new OrderDetail();
        orderDetail.setUuidOrder(UUID.randomUUID());
        orderDetail.setUuidProduct(UUID.randomUUID());
        orderDetail.setUuidDetail(UUID.randomUUID());
        orderDetail.setUnitPrice(BigDecimal.valueOf(50));
        orderDetail.setAmount(10);

        orderDetailRepository.save(orderDetail);
    }

    @Test
    void mongoDBContainerIsRunning() {
        assertTrue(mongoDBContainer.isCreated());
    }

    @Test
    @DisplayName("Should return orderDetail when uuid exists")
    void findByIdDetail_WhenUuidExists_ReturnsOrderDetail() {
        OrderDetail foundOrderDetail = orderDetailRepository.findByUuidDetail(orderDetail.getUuidDetail()).orElse(null);

        assertNotNull(foundOrderDetail);
        assertEquals(orderDetail.getUuidDetail(), foundOrderDetail.getUuidDetail());
        assertEquals(orderDetail.getUuidOrder(), foundOrderDetail.getUuidOrder());
        assertEquals(orderDetail.getUuidProduct(), foundOrderDetail.getUuidProduct());
        assertEquals(orderDetail.getUnitPrice(), foundOrderDetail.getUnitPrice());
        assertEquals(orderDetail.getAmount(), foundOrderDetail.getAmount());
    }

    @Test
    @DisplayName("Should return null when uuid does not exist")
    void findByIdDetail_WhenUuidDoesNotExist_ReturnsNull() {
        UUID nonExistentUuid = UUID.randomUUID();
        OrderDetail foundOrderDetail = orderDetailRepository.findByUuidDetail(nonExistentUuid).orElse(null);

        assertNull(foundOrderDetail);
    }
}