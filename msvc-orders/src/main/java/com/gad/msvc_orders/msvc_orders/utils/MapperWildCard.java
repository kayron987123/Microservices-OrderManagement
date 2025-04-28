package com.gad.msvc_orders.msvc_orders.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gad.msvc_orders.msvc_orders.dto.DataResponse;

public class MapperWildCard {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private MapperWildCard() {
    }

    public static <T> T toEntity(final DataResponse dataResponse, Class<T> entityType) {
        return objectMapper.convertValue(dataResponse.data(), entityType);
    }

}
