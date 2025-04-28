package com.gad.msvc_orders.msvc_orders.controller;

import com.gad.msvc_orders.msvc_orders.dto.OrderDTO;
import com.gad.msvc_orders.msvc_orders.enums.OrderStatusEnum;
import com.gad.msvc_orders.msvc_orders.exception.GlobalExceptionHandler;
import com.gad.msvc_orders.msvc_orders.exception.JwtDecodingException;
import com.gad.msvc_orders.msvc_orders.exception.KeyFactoryCreationException;
import com.gad.msvc_orders.msvc_orders.exception.OrderNotFoundException;
import com.gad.msvc_orders.msvc_orders.service.OrderService;
import com.gad.msvc_orders.msvc_orders.utils.UtilsMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private UUID uuidOrder;
    private UUID uuidOrderDetail;
    private OrderDTO orderDTO;
    private String tokenBearer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        tokenBearer = "Bearer token";
        uuidOrder = UUID.randomUUID();
        uuidOrderDetail = UUID.randomUUID();
        orderDTO = new OrderDTO(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UtilsMethods.dateTimeNowFormatted(),
                OrderStatusEnum.PENDING.name(), BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Should return status 201 and orderDto when token bearer is valid")
    void createOrder_WhenTokenBearerIsValid_ReturnsStatus201AndOrderDto() throws Exception {
        when(orderService.createOrder(anyString())).thenReturn(orderDTO);

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", tokenBearer))
                .andExpect(status().isCreated())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Order created"))
                .andExpect(jsonPath("$.data.uuid_order").value(orderDTO.uuidOrder()))
                .andExpect(jsonPath("$.data.uuid_customer").value(orderDTO.uuidCustomer()))
                .andExpect(jsonPath("$.data.order_date").value(orderDTO.orderDate()))
                .andExpect(jsonPath("$.data.status_order").value(orderDTO.statusOrder()))
                .andExpect(jsonPath("$.data.total_price").value(orderDTO.totalPrice()))
                .andExpect(jsonPath("$.timestamp").value(orderDTO.orderDate()))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(orderService, times(1)).createOrder(anyString());
    }

    @ParameterizedTest
    @CsvSource({
            "JWT could not be decoded, 401",
            "Error loading public key, 401",
            "Invalid public key, 500"
    })
    @DisplayName("Should handle different Jwt exceptions")
    void createOrder_WithVariousExceptions_ReturnsExpectedStatusAndMessage(String exceptionMessage, int expectedStatus) throws Exception {
        when(orderService.createOrder(anyString())).thenThrow(new JwtDecodingException(exceptionMessage));

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", tokenBearer))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.message").value(exceptionMessage))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").value(UtilsMethods.dateTimeNowFormatted()))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(orderService, times(1)).createOrder(anyString());
    }

    @Test
    @DisplayName("Should return status 500 and throw KeyFactoryCreationException when key factory for RSA cant be created")
    void createOrder_WhenKeyFactoryForRSACantBeCreated_ThrowsKeyFactoryCreationException() throws Exception {
        when(orderService.createOrder(anyString())).thenThrow(new KeyFactoryCreationException("Error creating Key factory for RSA"));

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", tokenBearer))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Error creating Key factory for RSA"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").value(UtilsMethods.dateTimeNowFormatted()))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(orderService, times(1)).createOrder(anyString());
    }

    @Test
    @DisplayName("Should return status 200 and orderDto when uuid is valid")
    void getOrderById_WhenUuidIsValid_ReturnsStatus200AndOrderDTo() throws Exception {
        when(orderService.findByUuid(anyString())).thenReturn(orderDTO);

        mockMvc.perform(get("/api/v1/orders/{uuid}", uuidOrder))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Order found"))
                .andExpect(jsonPath("$.data.uuid_order").value(orderDTO.uuidOrder()))
                .andExpect(jsonPath("$.data.uuid_customer").value(orderDTO.uuidCustomer()))
                .andExpect(jsonPath("$.data.order_date").value(orderDTO.orderDate()))
                .andExpect(jsonPath("$.data.status_order").value(orderDTO.statusOrder()))
                .andExpect(jsonPath("$.data.total_price").value(orderDTO.totalPrice()))
                .andExpect(jsonPath("$.timestamp").value(orderDTO.orderDate()))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(orderService, times(1)).findByUuid(anyString());
    }

    @Test
    @DisplayName("Should return status 404 and throw OrderNotFoundException when uuid is not found")
    void getOrderById_WhenUuidIsNotFound_ThrowsOrderNotFoundException() throws Exception {
        String uuidNotFound = UUID.randomUUID().toString();
        when(orderService.findByUuid(anyString())).thenThrow(new OrderNotFoundException("Order with uuid " + uuidNotFound + " not found"));

        mockMvc.perform(get("/api/v1/orders/{uuid}", uuidOrder))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order with uuid " + uuidNotFound + " not found"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").value(UtilsMethods.dateTimeNowFormatted()))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(orderService, times(1)).findByUuid(anyString());
    }

    @Test
    @DisplayName("Should return status 201 and orderDto when uuidOrderDetail and uuidOrder are valid")
    void updateOrder_WhenUuidOrderDetailAndUuidOrderAreValid_ReturnsStatus201AndOrderDto() throws Exception {
        when(orderService.updateTotalPrice(anyString(), anyString())).thenReturn(orderDTO);

        mockMvc.perform(put("/api/v1/orders")
                        .param("uuidOrderDetail", uuidOrderDetail.toString())
                        .param("uuidOrder", uuidOrder.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Order updated"))
                .andExpect(jsonPath("$.data.uuid_order").value(orderDTO.uuidOrder()))
                .andExpect(jsonPath("$.data.uuid_customer").value(orderDTO.uuidCustomer()))
                .andExpect(jsonPath("$.data.order_date").value(orderDTO.orderDate()))
                .andExpect(jsonPath("$.data.status_order").value(orderDTO.statusOrder()))
                .andExpect(jsonPath("$.data.total_price").value(orderDTO.totalPrice()))
                .andExpect(jsonPath("$.timestamp").value(orderDTO.orderDate()))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(orderService, times(1)).updateTotalPrice(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return status 404 and throw OrderNotFoundException when uuidOrderDetail is not found")
    void updateOrder_WhenUuidOrderDetailIsNotFound_ThrowsOrderNotFoundException() throws Exception {
        String uuidOrderDetailNotFound = UUID.randomUUID().toString();
        when(orderService.updateTotalPrice(anyString(), anyString())).thenThrow(new OrderNotFoundException("Order with uuid " + uuidOrderDetailNotFound + " not found"));

        mockMvc.perform(put("/api/v1/orders")
                        .param("uuidOrderDetail", uuidOrderDetail.toString())
                        .param("uuidOrder", uuidOrder.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order with uuid " + uuidOrderDetailNotFound + " not found"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").value(UtilsMethods.dateTimeNowFormatted()))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(orderService, times(1)).updateTotalPrice(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return status 204 when uuid exists")
    void deleteOrder_WhenUuidExists_ReturnsStatus204() throws Exception {
        doNothing().when(orderService).deleteOrder(anyString());

        mockMvc.perform(delete("/api/v1/orders/{uuid}", uuidOrder))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(anyString());
    }

    @Test
    @DisplayName("Should return status 404 when uuid not exists")
    void deleteOrder_WhenUuidNotExists_ReturnsStatus404() throws Exception {
        String uuidNotFound = UUID.randomUUID().toString();
        doThrow(new OrderNotFoundException("Order with uuid " + uuidNotFound + " not found")).when(orderService).deleteOrder(anyString());

        mockMvc.perform(delete("/api/v1/orders/{uuid}", uuidOrder))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order with uuid " + uuidNotFound + " not found"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").value(UtilsMethods.dateTimeNowFormatted()))
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(orderService, times(1)).deleteOrder(anyString());
    }
}