package com.gad.msvc_details_order.repository;

import com.gad.msvc_details_order.model.OrderDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderDetailRepository extends MongoRepository<OrderDetail, UUID> {
    Optional<OrderDetail> findByUuidDetail(UUID uuidDetail);
}
