package com.gad.msvc_details_order.service.feign;

import com.gad.msvc_details_order.config.feign.ProductFeignClient;
import com.gad.msvc_details_order.dto.DataResponse;
import com.gad.msvc_details_order.exception.ProductFeignNotFoundException;
import com.gad.msvc_details_order.model.Product;
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
class ProductServiceFeignTest {
    @Mock
    private ProductFeignClient productFeignClient;

    @InjectMocks
    private ProductServiceFeign productServiceFeign;

    @Test
    @DisplayName("Should return product when the UUID exists")
    void findProductByUuid_WhenUuidExists_ReturnsProduct() {
        String uuidProduct = "valid-uuid";
        DataResponse mockResponse = mock(DataResponse.class);
        Product expectedProduct = new Product();

        when(mockResponse.data()).thenReturn(expectedProduct);
        when(productFeignClient.findProductByUuid(uuidProduct)).thenReturn(mockResponse);

        Product result = productServiceFeign.findProductByUuid(uuidProduct);

        assertNotNull(result);
        assertEquals(expectedProduct, result);
        verify(productFeignClient, times(1)).findProductByUuid(uuidProduct);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when the UUID does not exist")
    void findProductByUuid_WhenUuidDoesNotExist_ThrowsProductNotFoundException() {
        String uuidProduct = "invalid-uuid";

        when(productFeignClient.findProductByUuid(uuidProduct)).thenReturn(null);

        Exception exception = assertThrows(ProductFeignNotFoundException.class, () ->
                productServiceFeign.findProductByUuid(uuidProduct));

        assertEquals("Feign: Product with UUID " + uuidProduct + " not found", exception.getMessage());
        verify(productFeignClient, times(1)).findProductByUuid(uuidProduct);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when a FeignException occurs")
    void findProductByUuid_WhenFeignExceptionOccurs_ThrowsProductNotFoundException() {
        String uuidProduct = "invalid-uuid";

        when(productFeignClient.findProductByUuid(uuidProduct)).thenThrow(FeignException.class);

        Exception exception = assertThrows(ProductFeignNotFoundException.class, () ->
                productServiceFeign.findProductByUuid(uuidProduct));

        assertEquals("Feign: Product information with UUID " + uuidProduct + " could not be obtained", exception.getMessage());
        verify(productFeignClient, times(1)).findProductByUuid(uuidProduct);
    }
}