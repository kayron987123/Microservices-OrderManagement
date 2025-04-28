package com.gad.msvc_oauth.config.feign;

import com.gad.msvc_oauth.dto.DataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "msvc-customer")
public interface CustomerFeignClient {
    @GetMapping("${custom.path}/customers/email/{email}")
    DataResponse findCustomerByEmail(@PathVariable String email);
}
