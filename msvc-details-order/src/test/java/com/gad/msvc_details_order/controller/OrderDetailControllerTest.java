package com.gad.msvc_details_order.controller;

import com.gad.msvc_details_order.dto.CreateOrderDetailRequest;
import com.gad.msvc_details_order.dto.OrderDetailDTO;
import com.gad.msvc_details_order.exception.*;
import com.gad.msvc_details_order.service.OrderDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderDetailControllerTest {
    private MockMvc mockMvc;

    @Mock
    private OrderDetailService orderDetailService;

    @InjectMocks
    private OrderDetailController orderDetailController;

    private CreateOrderDetailRequest createOrderDetailRequest;
    private OrderDetailDTO orderDetailDTO;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private UUID uuidOrder;

    @BeforeEach
    void setUp() {
        uuidOrder = UUID.randomUUID();
        mockMvc = MockMvcBuilders.standaloneSetup(orderDetailController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        createOrderDetailRequest = new CreateOrderDetailRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 10);
        orderDetailDTO = new OrderDetailDTO(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "Product Test", 10, BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Should return status 201 and return orderDetailDto when CreateOrderRequest is valid")
    void createOrderDetail_WhenCreateOrderRequestIsValid_ReturnsStatus201AndOrderDetailDto() throws Exception {
        when(orderDetailService.createOrderDetail(createOrderDetailRequest)).thenReturn(orderDetailDTO);

        mockMvc.perform(post("/api/v1/order-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDetailRequest)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Order detail created"))
                .andExpect(jsonPath("$.data.uuid_detail").value(orderDetailDTO.uuidDetail()))
                .andExpect(jsonPath("$.data.uuid_order").value(orderDetailDTO.uuidOrder()))
                .andExpect(jsonPath("$.data.product_name").value(orderDetailDTO.productName()))
                .andExpect(jsonPath("$.data.amount").value(orderDetailDTO.amount()))
                .andExpect(jsonPath("$.data.unit_price").value(orderDetailDTO.unitPrice()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());
        verify(orderDetailService, times(1)).createOrderDetail(createOrderDetailRequest);
    }

    @Test
    @DisplayName("Should return status 400 and throw MethodArgumentNotValidException when CreateOrderRequest is invalid")
    void createOrderDetail_WhenCreateOrderRequestIsInvalid_ReturnsStatus400AndThrowMethodArgumentNotValidException() throws Exception {
        CreateOrderDetailRequest createOrderDetailRequestInvalid = new CreateOrderDetailRequest(" ", "invalid-uuid", -1);

        mockMvc.perform(post("/api/v1/order-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDetailRequestInvalid)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation incorrect"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors[0].field").value("uuidOrder"))
                .andExpect(jsonPath("$.errors[0].messages[0]").value("Invalid UUID format"))
                .andExpect(jsonPath("$.errors[0].messages[1]").value("Order UUID cannot be empty"))
                .andExpect(jsonPath("$.errors[1].field").value("amount"))
                .andExpect(jsonPath("$.errors[1].messages[0]").value("The amount must be greater than 0"))
                .andExpect(jsonPath("$.errors[2].field").value("uuidProduct"))
                .andExpect(jsonPath("$.errors[2].messages[0]").value("Invalid UUID format"));

        verify(orderDetailService, never()).createOrderDetail(createOrderDetailRequestInvalid);
    }

    @Test
    @DisplayName("Should return status 404 and throw OrderNotFoundException when order not found")
    void createOrderDetail_WhenOrderNotFound_ReturnsStatus404AndThrowOrderNotFoundException() throws Exception {
        when(orderDetailService.createOrderDetail(createOrderDetailRequest)).thenThrow(new OrderNotFoundException("Order not found"));

        mockMvc.perform(post("/api/v1/order-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDetailRequest)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).createOrderDetail(createOrderDetailRequest);
    }

    @Test
    @DisplayName("Should return status 409 and throw StockNotAvailableException when amount exceed stock")
    void createOrderDetail_WhenAmountExceedStock_ReturnsStatus409AndThrowStockNotAvailableException() throws Exception {
        when(orderDetailService.createOrderDetail(createOrderDetailRequest)).thenThrow(new StockNotAvailableException("Stock not available"));

        mockMvc.perform(post("/api/v1/order-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDetailRequest)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Stock not available"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).createOrderDetail(createOrderDetailRequest);
    }

    @Test
    @DisplayName("Should return status 404 and throw OrderFeignNotFoundException when OrderServiceFeign not found order")
    void createOrderDetail_WhenOrderFeignNotFound_ReturnsStatus404AndThrowOrderFeignNotFoundException() throws Exception {
        when(orderDetailService.createOrderDetail(createOrderDetailRequest)).thenThrow(new OrderFeignNotFoundException("Feign: Order with UUID " + uuidOrder + " not found"));

        mockMvc.perform(post("/api/v1/order-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDetailRequest)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Feign: Order with UUID " + uuidOrder + " not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).createOrderDetail(createOrderDetailRequest);
    }

    @Test
    @DisplayName("Should return status 404 and throw OrderFeignNotFoundException when OrderServiceFeign could not be obtained")
    void createOrderDetail_WhenOrderFeignCouldNotBeObtained_ReturnsStatus404AndThrowOrderFeignNotFoundException() throws Exception {
        when(orderDetailService.createOrderDetail(createOrderDetailRequest)).thenThrow(new OrderFeignNotFoundException("Feign: Order information with UUID " + uuidOrder + " could not be obtained"));

        mockMvc.perform(post("/api/v1/order-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDetailRequest)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Feign: Order information with UUID " + uuidOrder + " could not be obtained"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).createOrderDetail(createOrderDetailRequest);
    }

    @Test
    @DisplayName("Should return status 404 and throw ProductFeignNotFoundException when ProductServiceFeign not found product")
    void createOrderDetail_WhenProductFeignNotFound_ReturnsStatus404AndThrowProductFeignNotFoundException() throws Exception {
        when(orderDetailService.createOrderDetail(createOrderDetailRequest)).thenThrow(new ProductFeignNotFoundException("Feign: Product with UUID " + createOrderDetailRequest.uuidProduct() + " not found"));

        mockMvc.perform(post("/api/v1/order-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDetailRequest)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Feign: Product with UUID " + createOrderDetailRequest.uuidProduct() + " not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).createOrderDetail(createOrderDetailRequest);
    }

    @Test
    @DisplayName("Should return status 404 and throw ProductFeignNotFoundException when ProductServiceFeign could not be obtained")
    void createOrderDetail_WhenProductFeignCouldNotBeObtained_ReturnsStatus404AndThrowProductFeignNotFoundException() throws Exception {
        when(orderDetailService.createOrderDetail(createOrderDetailRequest)).thenThrow(new ProductFeignNotFoundException("Feign: Product information with UUID " + createOrderDetailRequest.uuidProduct() + " could not be obtained"));

        mockMvc.perform(post("/api/v1/order-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDetailRequest)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Feign: Product information with UUID " + createOrderDetailRequest.uuidProduct() + " could not be obtained"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).createOrderDetail(createOrderDetailRequest);
    }

    @Test
    @DisplayName("Should return status 200 and return orderDetailDto when uuid is valid")
    void getOrderDetailByUuid_WhenUuidIsValid_ReturnsStatus200() throws Exception {
        when(orderDetailService.findOrderDetailByUuid(createOrderDetailRequest.uuidProduct())).thenReturn(orderDetailDTO);

        mockMvc.perform(get("/api/v1/order-details/" + createOrderDetailRequest.uuidProduct())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Order detail found"))
                .andExpect(jsonPath("$.data.uuid_detail").value(orderDetailDTO.uuidDetail()))
                .andExpect(jsonPath("$.data.uuid_order").value(orderDetailDTO.uuidOrder()))
                .andExpect(jsonPath("$.data.product_name").value(orderDetailDTO.productName()))
                .andExpect(jsonPath("$.data.amount").value(orderDetailDTO.amount()))
                .andExpect(jsonPath("$.data.unit_price").value(orderDetailDTO.unitPrice()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).findOrderDetailByUuid(createOrderDetailRequest.uuidProduct());
    }

    @Test
    @DisplayName("Should return status 404 and throw OrderNotFoundException when order detail not found")
    void getOrderDetailByUuid_WhenOrderDetailNotFound_ReturnsStatus404AndThrowOrderNotFoundException() throws Exception {
        when(orderDetailService.findOrderDetailByUuid(createOrderDetailRequest.uuidProduct())).thenThrow(new OrderNotFoundException("Order detail not found"));

        mockMvc.perform(get("/api/v1/order-details/" + createOrderDetailRequest.uuidProduct())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order detail not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).findOrderDetailByUuid(createOrderDetailRequest.uuidProduct());
    }

    @Test
    @DisplayName("Should return status 404 and throw OrderNotFoundException when ProductFeignService return product null")
    void getOrderDetailByUuid_WhenProductFeignServiceReturnProductNull_ReturnsStatus400AndThrowOrderNotFoundException() throws Exception {
        when(orderDetailService.findOrderDetailByUuid(createOrderDetailRequest.uuidProduct())).thenThrow(new OrderNotFoundException("Product with uuid " + createOrderDetailRequest.uuidProduct() + " not found"));

        mockMvc.perform(get("/api/v1/order-details/" + createOrderDetailRequest.uuidProduct())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product with uuid " + createOrderDetailRequest.uuidProduct() + " not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).findOrderDetailByUuid(createOrderDetailRequest.uuidProduct());
    }

    @Test
    @DisplayName("Should return 404 and throw ProductFeignNotFoundException when ProductServiceFeign not found product")
    void getOrderDetailByUuid_WhenProductFeignNotFound_ReturnsStatus404AndThrowProductFeignNotFoundException() throws Exception {
        when(orderDetailService.findOrderDetailByUuid(createOrderDetailRequest.uuidProduct())).thenThrow(new ProductFeignNotFoundException("Feign: Product with uuid " + createOrderDetailRequest.uuidProduct() + " not found"));

        mockMvc.perform(get("/api/v1/order-details/" + createOrderDetailRequest.uuidProduct())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Feign: Product with uuid " + createOrderDetailRequest.uuidProduct() + " not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).findOrderDetailByUuid(createOrderDetailRequest.uuidProduct());
    }

    @Test
    @DisplayName("Should return 404 and throw ProductFeignNotFoundException when ProductServiceFeign could not be obtained")
    void getOrderDetailByUuid_WhenProductFeignCouldNotBeObtained_ReturnsStatus404AndThrowProductFeignNotFoundException() throws Exception {
        when(orderDetailService.findOrderDetailByUuid(createOrderDetailRequest.uuidProduct())).thenThrow(new OrderNotFoundException("Feign: Product information with UUID " + createOrderDetailRequest.uuidProduct() + " could not be obtained"));

        mockMvc.perform(get("/api/v1/order-details/" + createOrderDetailRequest.uuidProduct())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Feign: Product information with UUID " + createOrderDetailRequest.uuidProduct() + " could not be obtained"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(orderDetailService, times(1)).findOrderDetailByUuid(createOrderDetailRequest.uuidProduct());
    }
}