package com.restaurant.management.order.repository;

import com.restaurant.management.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    java.util.Optional<Order> findByIdempotencyKey(String idempotencyKey);
}
