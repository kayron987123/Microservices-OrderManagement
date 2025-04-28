package com.gad.msvc_details_order.service;

import com.gad.msvc_details_order.dto.CreateOrderDetailRequest;
import com.gad.msvc_details_order.dto.OrderDetailDTO;
import com.gad.msvc_details_order.exception.OrderNotFoundException;
import com.gad.msvc_details_order.exception.ProductFeignNotFoundException;
import com.gad.msvc_details_order.exception.StockNotAvailableException;
import com.gad.msvc_details_order.model.Order;
import com.gad.msvc_details_order.model.OrderDetail;
import com.gad.msvc_details_order.model.Product;
import com.gad.msvc_details_order.repository.OrderDetailRepository;
import com.gad.msvc_details_order.service.feign.OrderServiceFeign;
import com.gad.msvc_details_order.service.feign.ProductServiceFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final ProductServiceFeign productServiceFeign;
    private final OrderServiceFeign orderServiceFeign;

    @Transactional
    public OrderDetailDTO createOrderDetail(CreateOrderDetailRequest createOrderDetailRequest) {
        Order order = orderServiceFeign.findOrderByUuid(createOrderDetailRequest.uuidOrder());
        if (order == null) {
            throw new OrderNotFoundException("Order with uuid " + createOrderDetailRequest.uuidOrder() + " not found");
        }
        Product product = productServiceFeign.findProductByUuid(createOrderDetailRequest.uuidProduct());
        if (product == null) {
            throw new ProductFeignNotFoundException("Product with uuid " + createOrderDetailRequest.uuidProduct() + " not found");
        }
        if (product.getStock() < createOrderDetailRequest.amount()) {
            throw new StockNotAvailableException("The amount entered " + createOrderDetailRequest.amount() + " exceeds the stock of the product " + product.getName() + " with stock " + product.getStock());
        }
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setUuidOrder(UUID.fromString(createOrderDetailRequest.uuidOrder()));
        orderDetail.setUuidProduct(UUID.fromString(createOrderDetailRequest.uuidProduct()));
        orderDetail.setAmount(createOrderDetailRequest.amount());
        orderDetail.setUnitPrice(product.getPrice());
        orderDetailRepository.save(orderDetail);

        return new OrderDetailDTO(
                orderDetail.getUuidDetail().toString(),
                orderDetail.getUuidOrder().toString(),
                product.getName(),
                orderDetail.getAmount(),
                orderDetail.getUnitPrice()
        );
    }

    @Cacheable(value = "OrderDetailByUuid", key = "#uuidOrderDetail")
    @Transactional(readOnly = true)
    public OrderDetailDTO findOrderDetailByUuid(UUID uuidOrderDetail) {
        OrderDetail orderDetail = orderDetailRepository.findByUuidDetail(uuidOrderDetail)
                .orElseThrow(() -> new OrderNotFoundException("Order detail with uuid " + uuidOrderDetail + " not found"));
        Product product = productServiceFeign.findProductByUuid(orderDetail.getUuidProduct().toString());
        if (product == null) {
            throw new ProductFeignNotFoundException("Product with uuid " + orderDetail.getUuidProduct() + " not found");
        }
        log.debug("Order detail found: {}", orderDetail);

        return new OrderDetailDTO(
                orderDetail.getUuidDetail().toString(),
                orderDetail.getUuidOrder().toString(),
                product.getName(),
                orderDetail.getAmount(),
                orderDetail.getUnitPrice()
        );
    }
}
