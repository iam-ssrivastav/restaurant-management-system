package com.restaurant.management.restaurant.controller;

import com.restaurant.management.restaurant.model.*;
import com.restaurant.management.restaurant.service.RestaurantCommandService;
import com.restaurant.management.restaurant.service.RestaurantQueryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <h2>RestaurantController</h2>
 * <p>
 * Implements CQRS pattern by delegating to Command and Query services.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/restaurants")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Restaurant Management", description = "Operations for menu, tables, and inventory")
public class RestaurantController {

    private final RestaurantQueryService queryService;
    private final RestaurantCommandService commandService;

    public RestaurantController(RestaurantQueryService queryService, RestaurantCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    @GetMapping("/menu")
    @PreAuthorize("hasRole('USER')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Get full menu (Cached)")
    public List<MenuItem> getMenu() {
        return queryService.getMenu();
    }

    @PostMapping("/menu")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Add new menu item (Evicts Cache)")
    public MenuItem addMenuItem(@RequestBody MenuItem item) {
        return commandService.addMenuItem(item);
    }

    @PostMapping("/tables")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Add a new table")
    public RestaurantTable addTable(@RequestBody RestaurantTable table) {
        return commandService.addTable(table);
    }

    @GetMapping("/tables")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public List<RestaurantTable> getTables() {
        return queryService.getTables();
    }

    @PatchMapping("/tables/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public RestaurantTable updateTableStatus(@PathVariable Long id, @RequestParam String status) {
        return commandService.updateTableStatus(id, status);
    }

    @GetMapping("/reservations")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public List<Reservation> getReservations() {
        return queryService.getReservations();
    }

    @PostMapping("/reservations")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return commandService.createReservation(reservation);
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public List<InventoryItem> getInventory() {
        return queryService.getInventory();
    }

    @PostMapping("/inventory")
    @PreAuthorize("hasRole('MANAGER')")
    public InventoryItem updateInventory(@RequestBody InventoryItem item) {
        return commandService.restockInventory(item);
    }

    @GetMapping("/health")
    public String health() {
        return "RMS Restaurant Service is functional.";
    }
}
