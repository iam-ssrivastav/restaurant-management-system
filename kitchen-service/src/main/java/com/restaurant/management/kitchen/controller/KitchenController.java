package com.restaurant.management.kitchen.controller;

import com.restaurant.management.kitchen.model.KitchenOrder;
import com.restaurant.management.kitchen.repository.KitchenOrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kitchen")
@Tag(name = "Kitchen Operations", description = "Back of house kitchen ticket management")
public class KitchenController {

    private final KitchenOrderRepository repository;
    private final org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;

    public KitchenController(KitchenOrderRepository repository,
            org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/tickets")
    @Operation(summary = "Get all active kitchen tickets")
    @PreAuthorize("hasRole('CHEF') or hasRole('ADMIN')")
    public List<KitchenOrder> getActiveTickets() {
        return repository.findAll();
    }

    @GetMapping("/tickets/{id}")
    @Operation(summary = "Get a kitchen ticket by ID")
    @PreAuthorize("hasRole('CHEF') or hasRole('ADMIN')")
    public KitchenOrder getTicket(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PatchMapping("/tickets/{id}/status")
    @Operation(summary = "Update ticket preparation status")
    @PreAuthorize("hasRole('CHEF')")
    public KitchenOrder updateStatus(@PathVariable Long id, @RequestParam String status) {
        KitchenOrder order = repository.findById(id).orElseThrow();
        order.setStatus(status);
        KitchenOrder saved = repository.save(order);

        if ("READY".equalsIgnoreCase(status)) {
            kafkaTemplate.send("order-ready", String.valueOf(order.getOrderId()),
                    String.format("{\"orderId\":%d, \"status\":\"READY\"}", order.getOrderId()));
        }

        return saved;
    }
}
