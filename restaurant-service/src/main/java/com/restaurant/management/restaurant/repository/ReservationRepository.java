package com.restaurant.management.restaurant.repository;

import com.restaurant.management.restaurant.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByTableId(Long tableId);

    java.util.Optional<Reservation> findByIdempotencyKey(String idempotencyKey);
}
