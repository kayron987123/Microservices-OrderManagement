package com.gad.msvc_orders.msvc_orders.service.feign;

import com.gad.msvc_orders.msvc_orders.config.feign.OrderDetailFeignClient;
import com.gad.msvc_orders.msvc_orders.dto.DataResponse;
import com.gad.msvc_orders.msvc_orders.exception.OrderDetailFeignNotFoundException;
import com.gad.msvc_orders.msvc_orders.model.OrderDetail;
import com.gad.msvc_orders.msvc_orders.utils.MapperWildCard;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailServiceFeign {
    private final OrderDetailFeignClient orderDetailFeignClient;

    @Cacheable(value = "OrderDetailByUuid", key = "#uuidOrderDetail")
    public OrderDetail findOrderDetailByUuid(String uuidOrderDetail) {
        try {
            DataResponse dataResponse = orderDetailFeignClient.findOrderDetailByUuid(uuidOrderDetail);
            if (dataResponse == null || dataResponse.data() == null) {
                throw new OrderDetailFeignNotFoundException("Feign: Order detail with uuid " + uuidOrderDetail + " not found");
            }

            return MapperWildCard.toEntity(dataResponse, OrderDetail.class);
        } catch (FeignException e) {
            throw new OrderDetailFeignNotFoundException("Feign: Order detail information with uuid " + uuidOrderDetail + " could not be obtained", e);
        }
    }
}
