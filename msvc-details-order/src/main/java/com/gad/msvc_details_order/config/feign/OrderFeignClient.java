package com.gad.msvc_details_order.config.feign;

import com.gad.msvc_details_order.dto.DataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-orders")
public interface OrderFeignClient {
    @GetMapping("${custom.path}/orders/{uuidOrder}")
    DataResponse findOrderByUuid(@PathVariable String uuidOrder);
}
