package com.gad.msvc_orders.msvc_orders.service.feign;

import com.gad.msvc_orders.msvc_orders.config.feign.OrderDetailFeignClient;
import com.gad.msvc_orders.msvc_orders.dto.DataResponse;
import com.gad.msvc_orders.msvc_orders.exception.OrderDetailFeignNotFoundException;
import com.gad.msvc_orders.msvc_orders.model.OrderDetail;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceFeignTest {

    @Mock
    private OrderDetailFeignClient orderDetailFeignClient;

    @InjectMocks
    private OrderDetailServiceFeign orderDetailServiceFeign;

    @Test
    @DisplayName("Should return orderDetail when the UUID exists")
    void findOrderDetailByUuid_WhenUuidExists_ReturnsOrderDetail() {
        String uuidOrderDetail = "valid-uuid";
        DataResponse mockResponse = mock(DataResponse.class);
        OrderDetail expectedOrderDetail = new OrderDetail();

        when(mockResponse.data()).thenReturn(expectedOrderDetail);
        when(orderDetailFeignClient.findOrderDetailByUuid(uuidOrderDetail)).thenReturn(mockResponse);

        OrderDetail result = orderDetailServiceFeign.findOrderDetailByUuid(uuidOrderDetail);

        assertNotNull(result);
        assertEquals(expectedOrderDetail, result);
        verify(orderDetailFeignClient, times(1)).findOrderDetailByUuid(uuidOrderDetail);
    }

    @Test
    @DisplayName("Should throw OrderDetailFeignNotFoundException when the UUID does not exist")
    void findOrderDetailByUuid_WhenUuidDoesNotExist_ThrowsOrderDetailNotFoundException() {
        String uuidOrderDetail = "invalid-uuid";

        when(orderDetailFeignClient.findOrderDetailByUuid(uuidOrderDetail)).thenReturn(null);

        Exception exception = assertThrows(OrderDetailFeignNotFoundException.class, () ->
                orderDetailServiceFeign.findOrderDetailByUuid(uuidOrderDetail));

        assertEquals("Feign: Order detail with uuid " + uuidOrderDetail + " not found", exception.getMessage());
        verify(orderDetailFeignClient, times(1)).findOrderDetailByUuid(uuidOrderDetail);
    }

    @Test
    @DisplayName("Should throw OrderDetailFeignNotFoundException when a feignException occurs")
    void findOrderDetailByUuid_WhenFeignExceptionOccurs_ThrowsOrderDetailNotFoundException() {
        String uuidOrderDetail = "uuid-causing-feign-exception";

        when(orderDetailFeignClient.findOrderDetailByUuid(uuidOrderDetail)).thenThrow(FeignException.class);

        Exception exception = assertThrows(OrderDetailFeignNotFoundException.class, () ->
                orderDetailServiceFeign.findOrderDetailByUuid(uuidOrderDetail));

        assertEquals("Feign: Order detail information with uuid " + uuidOrderDetail + " could not be obtained", exception.getMessage());
        verify(orderDetailFeignClient, times(1)).findOrderDetailByUuid(uuidOrderDetail);
    }
}