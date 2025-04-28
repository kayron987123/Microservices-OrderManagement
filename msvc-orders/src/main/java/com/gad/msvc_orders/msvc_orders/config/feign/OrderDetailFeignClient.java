package com.gad.msvc_orders.msvc_orders.config.feign;

import com.gad.msvc_orders.msvc_orders.dto.DataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "msvc-details-order")
public interface OrderDetailFeignClient {
    @GetMapping("${custom.path}/order-details/{uuid}")
    DataResponse findOrderDetailByUuid(@PathVariable String uuid);
}
