package com.restaurant.management.delivery.controller;

import com.restaurant.management.delivery.model.DeliveryTask;
import com.restaurant.management.delivery.repository.DeliveryTaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery")
@Tag(name = "Delivery Management", description = "Dispatcher and courier operations")
public class DeliveryController {

    private final DeliveryTaskRepository repository;

    public DeliveryController(DeliveryTaskRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/tasks")
    @Operation(summary = "Get all delivery tasks")
    @PreAuthorize("hasRole('DELIVERY') or hasRole('ADMIN')")
    public List<DeliveryTask> getAllTasks() {
        return repository.findAll();
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "Get a delivery task by ID")
    @PreAuthorize("hasRole('DELIVERY') or hasRole('ADMIN')")
    public DeliveryTask getDeliveryTask(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PatchMapping("/tasks/{id}/dispatch")
    @Operation(summary = "Mark task as DISPATCHED and assign courier")
    @PreAuthorize("hasRole('DELIVERY')")
    public DeliveryTask dispatchTask(@PathVariable Long id, @RequestParam String courierName) {
        DeliveryTask task = repository.findById(id).orElseThrow();
        task.setStatus("DISPATCHED");
        task.setCourierName(courierName);
        return repository.save(task);
    }

    @PatchMapping("/tasks/{id}/complete")
    @Operation(summary = "Mark task as DELIVERED")
    @PreAuthorize("hasRole('DELIVERY')")
    public DeliveryTask completeTask(@PathVariable Long id) {
        DeliveryTask task = repository.findById(id).orElseThrow();
        task.setStatus("DELIVERED");
        return repository.save(task);
    }
}
