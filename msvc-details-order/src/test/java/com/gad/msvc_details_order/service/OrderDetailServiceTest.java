package com.gad.msvc_details_order.service;

import com.gad.msvc_details_order.dto.CreateOrderDetailRequest;
import com.gad.msvc_details_order.dto.OrderDetailDTO;
import com.gad.msvc_details_order.exception.OrderNotFoundException;
import com.gad.msvc_details_order.exception.StockNotAvailableException;
import com.gad.msvc_details_order.model.Order;
import com.gad.msvc_details_order.model.OrderDetail;
import com.gad.msvc_details_order.model.Product;
import com.gad.msvc_details_order.repository.OrderDetailRepository;
import com.gad.msvc_details_order.service.feign.OrderServiceFeign;
import com.gad.msvc_details_order.service.feign.ProductServiceFeign;
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
class OrderDetailServiceTest {
    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private ProductServiceFeign productServiceFeign;

    @Mock
    private OrderServiceFeign orderServiceFeign;

    @InjectMocks
    private OrderDetailService orderDetailService;

    private CreateOrderDetailRequest createOrderDetailRequest;
    private Order order;
    private Product product;
    private OrderDetail orderDetail;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setUuidOrder(UUID.randomUUID());

        product = new Product();
        product.setUuidProduct(UUID.randomUUID());
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(20);

        orderDetail = new OrderDetail();
        orderDetail.setAmount(10);
        orderDetail.setUnitPrice(BigDecimal.valueOf(100));
        orderDetail.setUuidProduct(product.getUuidProduct());
        orderDetail.setUuidOrder(order.getUuidOrder());

        createOrderDetailRequest = new CreateOrderDetailRequest(order.getUuidOrder().toString(), product.getUuidProduct().toString(), 10);
    }

    @Test
    @DisplayName("Should save orderDetail and return orderDetailDTO when CreateOrderRequest is sent")
    void createOrderDetail_WhenCreateOrderRequest_ReturnsOrderDetailDtoAndSaveOrderDetail() {
        when(orderServiceFeign.findOrderByUuid(any())).thenReturn(order);
        when(productServiceFeign.findProductByUuid(any())).thenReturn(product);
        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(orderDetail);

        OrderDetailDTO orderDetailDTO =  orderDetailService.createOrderDetail(createOrderDetailRequest);

        assertNotNull(orderDetailDTO);
        assertNotNull(orderDetailDTO.uuidDetail());
        assertEquals(orderDetail.getUuidOrder().toString(), orderDetailDTO.uuidOrder());
        assertEquals(product.getName(), orderDetailDTO.productName());
        assertEquals(orderDetail.getAmount(), orderDetailDTO.amount());
        assertEquals(orderDetail.getUnitPrice(), orderDetailDTO.unitPrice());
        assertEquals(product.getPrice(), orderDetailDTO.unitPrice());
        assertEquals(orderDetail.getAmount(), orderDetailDTO.amount());
        verify(orderDetailRepository, times(1)).save(any(OrderDetail.class));
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order does not exist")
    void createOrderDetail_WhenOrderDoesNotExist_ThrowsException() {
        when(orderServiceFeign.findOrderByUuid(any())).thenReturn(null);

        Exception exception =  assertThrows(OrderNotFoundException.class, () -> orderDetailService.createOrderDetail(createOrderDetailRequest));
        assertEquals("Order with uuid " + createOrderDetailRequest.uuidOrder() + " not found", exception.getMessage());
        verify(orderDetailRepository, never()).save(any(OrderDetail.class));
    }

    @Test
    @DisplayName("Should throw StockNotAvailableException when product stock is not available")
    void createOrderDetail_WhenProductStockNotAvailable_ThrowsException() {
        CreateOrderDetailRequest createOrderDetailRequestException = new CreateOrderDetailRequest(order.getUuidOrder().toString(), product.getUuidProduct().toString(), 50);

        when(orderServiceFeign.findOrderByUuid(any())).thenReturn(order);
        when(productServiceFeign.findProductByUuid(any())).thenReturn(product);

        Exception exception = assertThrows(StockNotAvailableException.class, () -> orderDetailService.createOrderDetail(createOrderDetailRequestException));
        assertEquals("The amount entered 50 exceeds the stock of the product Test Product with stock 20", exception.getMessage());
        verify(orderDetailRepository, never()).save(any(OrderDetail.class));
    }

    @Test
    @DisplayName("Should return OrderDetailDTO when uuid exists")
    void findOrderDetailByUuid_WhenUuidExists_ReturnsOrderDetailDTO() {
        UUID uuidOrderDetail = orderDetail.getUuidDetail();
        when(productServiceFeign.findProductByUuid(any())).thenReturn(product);
        when(orderDetailRepository.findByUuidDetail(uuidOrderDetail)).thenReturn(Optional.of(orderDetail));

        OrderDetailDTO orderDetailDTO = orderDetailService.findOrderDetailByUuid(uuidOrderDetail.toString());
        System.out.println(orderDetailDTO);
        System.out.println(orderDetail);
        System.out.println(product);

        assertNotNull(orderDetailDTO);
        assertEquals(orderDetail.getUuidDetail().toString(), orderDetailDTO.uuidDetail());
        assertEquals(orderDetail.getUuidOrder().toString(), orderDetailDTO.uuidOrder());
        assertEquals(order.getUuidOrder().toString(), orderDetailDTO.uuidOrder());
        assertEquals(product.getName(), orderDetailDTO.productName());
        assertEquals(orderDetail.getAmount(), orderDetailDTO.amount());
        assertEquals(orderDetail.getUnitPrice(), orderDetailDTO.unitPrice());
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when uuid does not exist")
    void findOrderDetailByUuid_WhenUuidDoesNotExist_ThrowsOrderNotFoundException() {
        UUID uuidOrderDetail = UUID.randomUUID();
        when(orderDetailRepository.findByUuidDetail(uuidOrderDetail)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderNotFoundException.class, () -> orderDetailService.findOrderDetailByUuid(uuidOrderDetail.toString()));

        assertEquals("Order detail with uuid " + uuidOrderDetail + " not found", exception.getMessage());
        verify(orderDetailRepository, times(1)).findByUuidDetail(uuidOrderDetail);
    }
}