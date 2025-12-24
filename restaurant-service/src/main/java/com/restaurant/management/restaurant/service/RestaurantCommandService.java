package com.restaurant.management.restaurant.service;

import com.restaurant.management.restaurant.model.MenuItem;
import com.restaurant.management.restaurant.model.Reservation;
import com.restaurant.management.restaurant.model.RestaurantTable;
import com.restaurant.management.restaurant.model.InventoryItem;
import com.restaurant.management.restaurant.repository.MenuItemRepository;
import com.restaurant.management.restaurant.repository.ReservationRepository;
import com.restaurant.management.restaurant.repository.RestaurantTableRepository;
import com.restaurant.management.restaurant.repository.InventoryItemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CQRS - Command Service for Write Operations
 */
@Service
@Transactional
public class RestaurantCommandService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantTableRepository tableRepository;
    private final InventoryItemRepository inventoryRepository;
    private final ReservationRepository reservationRepository;

    public RestaurantCommandService(MenuItemRepository menuItemRepository,
            RestaurantTableRepository tableRepository,
            InventoryItemRepository inventoryRepository,
            ReservationRepository reservationRepository) {
        this.menuItemRepository = menuItemRepository;
        this.tableRepository = tableRepository;
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
    }

    @CacheEvict(value = "menu", allEntries = true)
    public MenuItem addMenuItem(MenuItem item) {
        return menuItemRepository.save(item);
    }

    public RestaurantTable addTable(RestaurantTable table) {
        table.setStatus("AVAILABLE");
        return tableRepository.save(table);
    }

    public RestaurantTable updateTableStatus(Long id, String status) {
        RestaurantTable table = tableRepository.findById(id).orElseThrow();
        table.setStatus(status);
        return tableRepository.save(table);
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestaurantCommandService.class);

    public Reservation createReservation(Reservation reservation) {
        if (reservation.getIdempotencyKey() != null) {
            java.util.Optional<Reservation> existing = reservationRepository
                    .findByIdempotencyKey(reservation.getIdempotencyKey());
            if (existing.isPresent()) {
                log.info("Reservation: Duplicate request detected for key: {}. Returning existing reservation #{}",
                        reservation.getIdempotencyKey(), existing.get().getId());
                return existing.get();
            }
        }

        tableRepository.findById(reservation.getTableId()).ifPresent(table -> {
            table.setStatus("RESERVED");
            tableRepository.save(table);
        });
        reservation.setStatus("BOOKED");
        log.info("Reservation: Creating new booking for guest: {}", reservation.getGuestName());
        return reservationRepository.save(reservation);
    }

    public InventoryItem restockInventory(InventoryItem item) {
        return inventoryRepository.save(item);
    }
}
