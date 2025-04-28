package com.gad.msvc_products.assembler;

import com.gad.msvc_products.controller.ProductController;
import com.gad.msvc_products.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssembler implements RepresentationModelAssembler<ProductDTO, EntityModel<ProductDTO>> {

    @Value("${custom.path}")
    private String customPath;

    @Override
    @NonNull
    public EntityModel<ProductDTO> toModel(@NonNull ProductDTO product) {
        return EntityModel.of(product,
                linkTo(methodOn(ProductController.class).getProductByUuid(product.uuidProduct().toString())).withSelfRel().withHref(customPath + "/products/" + product.uuidProduct()));
    }
}
