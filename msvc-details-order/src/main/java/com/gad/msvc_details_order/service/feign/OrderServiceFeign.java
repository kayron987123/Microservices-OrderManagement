package com.gad.msvc_details_order.service.feign;

import com.gad.msvc_details_order.config.feign.OrderFeignClient;
import com.gad.msvc_details_order.dto.DataResponse;
import com.gad.msvc_details_order.exception.OrderFeignNotFoundException;
import com.gad.msvc_details_order.model.Order;
import com.gad.msvc_details_order.utils.MapperWildCard;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceFeign {
    private final OrderFeignClient orderFeignClient;

    @Cacheable(value = "OrderByUuid", key = "#uuidOrder")
    public Order findOrderByUuid(String uuidOrder) {
        try {
            DataResponse dataResponse = orderFeignClient.findOrderByUuid(uuidOrder);

            if (dataResponse == null || dataResponse.data() == null) {
                throw new OrderFeignNotFoundException("Feign: Order with UUID " + uuidOrder + " not found");
            }
            return MapperWildCard.toEntity(dataResponse, Order.class);
        }catch (FeignException e) {
            throw new OrderFeignNotFoundException("Feign: Order information with UUID " + uuidOrder + " could not be obtained", e);
        }
    }
}
