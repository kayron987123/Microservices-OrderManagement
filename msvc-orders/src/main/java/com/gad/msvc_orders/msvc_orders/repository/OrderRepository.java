package com.gad.msvc_orders.msvc_orders.repository;

import com.gad.msvc_orders.msvc_orders.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends MongoRepository<Order, UUID> {
    Optional<Order> findOrderByUuid(UUID uuidOrder);
}
