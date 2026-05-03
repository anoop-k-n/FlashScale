package com.anoopkn.ticketsystem.repositories;

import com.anoopkn.ticketsystem.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
