package com.gad.msvc_details_order.service.feign;

import com.gad.msvc_details_order.config.feign.ProductFeignClient;
import com.gad.msvc_details_order.dto.DataResponse;
import com.gad.msvc_details_order.exception.ProductFeignNotFoundException;
import com.gad.msvc_details_order.model.Product;
import com.gad.msvc_details_order.utils.MapperWildCard;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceFeign {
    private final ProductFeignClient productFeignClient;

    @Cacheable(value = "ProductByUuid", key = "#uuidProduct")
    public Product findProductByUuid(String uuidProduct) {
        try {
            DataResponse dataResponse = productFeignClient.findProductByUuid(uuidProduct);

            if (dataResponse == null || dataResponse.data() == null) {
                throw new ProductFeignNotFoundException("Feign: Product with UUID " + uuidProduct + " not found");
            }
            return MapperWildCard.toEntity(dataResponse, Product.class);
        }catch (FeignException e) {
            throw new ProductFeignNotFoundException("Feign: Product information with UUID " + uuidProduct + " could not be obtained", e);
        }
    }
}
