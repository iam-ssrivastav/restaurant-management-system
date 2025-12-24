package com.restaurant.management.order.controller;

import com.restaurant.management.order.model.Order;
import com.restaurant.management.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Shivam Srivastav
 *         REST controller for Order operations.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @CircuitBreaker(name = "orderService", fallbackMethod = "placeOrderFallback")
    @Retry(name = "orderService")
    @io.swagger.v3.oas.annotations.Operation(summary = "Place a new guest order")
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        log.info("API Request: Place Order received");
        return ResponseEntity.ok(orderService.placeOrder(order));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "View order details by ID")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @io.swagger.v3.oas.annotations.Operation(summary = "List all orders in the system")
    public ResponseEntity<java.util.List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Cancel a pending order")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order #" + id + " has been cancelled.");
    }

    public ResponseEntity<Order> placeOrderFallback(Order order, Throwable t) {
        log.error("Circuit Breaker: Order placement failed. Reason: {}", t.getMessage());
        return ResponseEntity.status(503).build();
    }

    @GetMapping("/health")
    public String health() {
        return "Order Service is Up and Running!";
    }
}
