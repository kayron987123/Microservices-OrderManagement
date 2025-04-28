package com.gad.msvc_details_order.config.feign;

import com.gad.msvc_details_order.dto.DataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "msvc-products")
public interface ProductFeignClient {
    @GetMapping("${custom.path}/products/{uuid_product}")
    DataResponse findProductByUuid(@PathVariable("uuid_product") String uuidProduct);
}
