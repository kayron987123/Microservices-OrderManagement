package com.gad.msvc_details_order.service.feign;

import com.gad.msvc_details_order.config.feign.OrderFeignClient;
import com.gad.msvc_details_order.dto.DataResponse;
import com.gad.msvc_details_order.exception.OrderFeignNotFoundException;
import com.gad.msvc_details_order.model.Order;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceFeignTest {
    @Mock
    private OrderFeignClient orderFeignClient;

    @InjectMocks
    private OrderServiceFeign orderServiceFeign;

    @Test
    @DisplayName("Should return order when the UUID exists")
    void findOrderByUuid_WhenUuidExists_ReturnsOrder() {
        String uuidOrderDetail = "valid-uuid";
        DataResponse mockResponse = mock(DataResponse.class);
        Order expectedOrder = new Order();

        when(mockResponse.data()).thenReturn(expectedOrder);
        when(orderFeignClient.findOrderByUuid(uuidOrderDetail)).thenReturn(mockResponse);

        Order result = orderServiceFeign.findOrderByUuid(uuidOrderDetail);

        assertNotNull(result);
        assertEquals(expectedOrder, result);
        verify(orderFeignClient, times(1)).findOrderByUuid(uuidOrderDetail);
    }

    @Test
    @DisplayName("Should throw orderNotFoundException when the UUID does not exist")
    void findOrderByUuid_WhenUuidDoesNotExist_ThrowsOrderNotFoundException() {
        String uuidOrderDetail = "invalid-uuid";

        when(orderFeignClient.findOrderByUuid(uuidOrderDetail)).thenReturn(null);

        Exception exception = assertThrows(OrderFeignNotFoundException.class, () ->
                orderServiceFeign.findOrderByUuid(uuidOrderDetail));

        assertEquals("Feign: Order with UUID " + uuidOrderDetail + " not found", exception.getMessage());
        verify(orderFeignClient, times(1)).findOrderByUuid(uuidOrderDetail);
    }

    @Test
    @DisplayName("Should throw orderNotFoundException when a feignException occurs")
    void findOrderByUuid_WhenFeignExceptionOccurs_ThrowsOrderNotFoundException() {
        String uuidOrderDetail = "invalid-uuid";

        when(orderFeignClient.findOrderByUuid(uuidOrderDetail)).thenThrow(FeignException.class);

        Exception exception = assertThrows(OrderFeignNotFoundException.class, () ->
                orderServiceFeign.findOrderByUuid(uuidOrderDetail));

        assertEquals("Feign: Order information with UUID " + uuidOrderDetail + " could not be obtained", exception.getMessage());
        verify(orderFeignClient, times(1)).findOrderByUuid(uuidOrderDetail);
    }
}