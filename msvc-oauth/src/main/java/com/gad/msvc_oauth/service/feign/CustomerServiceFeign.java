package com.gad.msvc_oauth.service.feign;

import com.gad.msvc_oauth.config.feign.CustomerFeignClient;
import com.gad.msvc_oauth.dto.DataResponse;
import com.gad.msvc_oauth.exception.CustomerFeignNotFoundException;
import com.gad.msvc_oauth.model.Customer;
import com.gad.msvc_oauth.utils.CustomerMapper;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceFeign {
    private final CustomerFeignClient customerFeignClient;

    @CircuitBreaker(name = "CustomerByEmailCircuitBreaker", fallbackMethod = "findByEmailFallback")
    @Retry(name = "CustomerByEmailRetry", fallbackMethod = "findByEmailFallback")
    @Cacheable(value = "CustomerByEmail", key = "#email")
    public Customer findByEmail(String email) {
        try {
            DataResponse dataResponse = customerFeignClient.findCustomerByEmail(email);

            if (dataResponse == null || dataResponse.data() == null) {
                throw new CustomerFeignNotFoundException("Feign: Customer with email " + email + " not found");
            }

            return CustomerMapper.toCustomer(dataResponse);
        } catch (FeignException e) {
            log.error("Feign error when retrieving customer by email {}: {}", email, e.getMessage());
            throw new CustomerFeignNotFoundException("Feign: Customer information with email " + email + " could not be obtained", e);
        }
    }

    public Customer findByEmailFallback(String email, Throwable ex) {
        log.warn("Fallback triggered for findByEmail with email {}. Reason: {}", email, ex.getMessage());
        throw new CustomerFeignNotFoundException("Feign: Customer service is currently unavailable. Please try again later.");
    }
}
