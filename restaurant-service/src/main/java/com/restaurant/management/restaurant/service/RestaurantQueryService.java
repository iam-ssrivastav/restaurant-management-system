package com.restaurant.management.restaurant.service;

import com.restaurant.management.restaurant.model.MenuItem;
import com.restaurant.management.restaurant.model.Reservation;
import com.restaurant.management.restaurant.model.RestaurantTable;
import com.restaurant.management.restaurant.model.InventoryItem;
import com.restaurant.management.restaurant.repository.MenuItemRepository;
import com.restaurant.management.restaurant.repository.ReservationRepository;
import com.restaurant.management.restaurant.repository.RestaurantTableRepository;
import com.restaurant.management.restaurant.repository.InventoryItemRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * CQRS - Query Service for Read Operations
 */
@Service
public class RestaurantQueryService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantTableRepository tableRepository;
    private final InventoryItemRepository inventoryRepository;
    private final ReservationRepository reservationRepository;

    public RestaurantQueryService(MenuItemRepository menuItemRepository,
            RestaurantTableRepository tableRepository,
            InventoryItemRepository inventoryRepository,
            ReservationRepository reservationRepository) {
        this.menuItemRepository = menuItemRepository;
        this.tableRepository = tableRepository;
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
    }

    @Cacheable(value = "menu", key = "'all_items'")
    public List<MenuItem> getMenu() {
        return menuItemRepository.findAll();
    }

    public List<RestaurantTable> getTables() {
        return tableRepository.findAll();
    }

    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    public List<InventoryItem> getInventory() {
        return inventoryRepository.findAll();
    }
}
