package com.gad.msvc_oauth.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gad.msvc_oauth.dto.DataResponse;
import com.gad.msvc_oauth.model.Customer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private CustomerMapper() {
    }

    public static Customer toCustomer(final DataResponse dataResponse) {
        return objectMapper.convertValue(dataResponse.data(), Customer.class);
    }
}
