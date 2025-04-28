package com.gad.msvc_oauth.service.feign;

import com.gad.msvc_oauth.config.feign.CustomerFeignClient;
import com.gad.msvc_oauth.dto.DataResponse;
import com.gad.msvc_oauth.exception.CustomerFeignNotFoundException;
import com.gad.msvc_oauth.model.Customer;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceFeignTest {

    @Mock
    private CustomerFeignClient customerFeignClient;

    @InjectMocks
    private CustomerServiceFeign customerServiceFeign;

    @Test
    @DisplayName("Should return customer when the email exists")
    void findByEmail_WhenEmailExists_ReturnsCustomer() {
        String email = "qwerty@gmail.com";
        DataResponse mockResponse = mock(DataResponse.class);
        Customer customerExpected = new Customer();

        when(mockResponse.data()).thenReturn(customerExpected);
        when(customerFeignClient.findCustomerByEmail(anyString())).thenReturn(mockResponse);

        Customer customerResult = customerServiceFeign.findByEmail(email);

        assertNotNull(customerResult);
        assertEquals(customerExpected, customerResult);
        verify(customerFeignClient, times(1)).findCustomerByEmail(email);
    }

    @Test
    @DisplayName("Should throw CustomerFeignNotFoundException when the email does not exist")
    void findByEmail_WhenEmailDoesNotExist_ThrowsCustomerFeignNotFoundException() {
        String email = "email-not-exist";

        when(customerFeignClient.findCustomerByEmail(anyString())).thenReturn(null);

        Exception exception = assertThrows(CustomerFeignNotFoundException.class, () ->
                customerServiceFeign.findByEmail(email));

        assertEquals("Feign: Customer with email " + email + " not found", exception.getMessage());
        verify(customerFeignClient, times(1)).findCustomerByEmail(email);
    }

    @Test
    @DisplayName("Should throw CustomerFeignNotFoundException when a FeignException occurs")
    void findByEmail_WhenFeignExceptionOccurs_ThrowsCustomerFeignNotFoundException() {
        String email = "email-not-exist";

        when(customerFeignClient.findCustomerByEmail(anyString())).thenThrow(FeignException.class);

        Exception exception = assertThrows(CustomerFeignNotFoundException.class, () ->
                customerServiceFeign.findByEmail(email));

        assertEquals("Feign: Customer information with email " + email + " could not be obtained", exception.getMessage());
        verify(customerFeignClient, times(1)).findCustomerByEmail(email);
    }
}