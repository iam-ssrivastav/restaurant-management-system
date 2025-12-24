package com.restaurant.management.order.service;

import com.restaurant.management.order.model.Order;
import com.restaurant.management.order.outbox.OutboxEvent;
import com.restaurant.management.order.outbox.OutboxRepository;
import com.restaurant.management.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <h2>OrderService</h2>
 * <p>
 * Core service for processing guest orders within the Restaurant Management
 * System.
 * High-reliability event publishing is achieved through the Transactional
 * Outbox Pattern.
 * </p>
 * 
 * @author Shivam Srivastav
 */
@Service
public class OrderService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;

    public OrderService(OrderRepository orderRepository, OutboxRepository outboxRepository) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public Order placeOrder(Order order) {
        if (order.getIdempotencyKey() != null) {
            java.util.Optional<Order> existingOrder = orderRepository.findByIdempotencyKey(order.getIdempotencyKey());
            if (existingOrder.isPresent()) {
                log.info(
                        "Order Processing: Duplicate request detected for idempotency key: {}. Returning existing order #{}",
                        order.getIdempotencyKey(), existingOrder.get().getId());
                return existingOrder.get();
            }
        } else {
            log.warn("Order Processing: Request received without an idempotencyKey. Duplicates will NOT be blocked.");
        }

        log.info("Order Processing: Creating new order for guest: {}", order.getCustomerName());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }

        Order savedOrder = orderRepository.save(order);

        OutboxEvent event = new OutboxEvent(
                null,
                savedOrder.getId().toString(),
                "ORDER",
                "order-created",
                String.format("{\"orderId\":%d, \"amount\":\"%s\"}", savedOrder.getId(), savedOrder.getTotalAmount()),
                false,
                java.time.LocalDateTime.now());

        outboxRepository.save(event);
        log.info("Order Processing: Order #{} recorded in database and outbox.", savedOrder.getId());

        return savedOrder;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    public java.util.List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        if ("PENDING".equals(order.getStatus())) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            log.info("Order Processing: Order #{} has been cancelled.", id);
        } else {
            throw new IllegalStateException("Order cannot be cancelled in its current state: " + order.getStatus());
        }
    }
}
