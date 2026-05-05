package com.anoopkn.ticketsystem.services;

import com.anoopkn.ticketsystem.entities.Event;
import com.anoopkn.ticketsystem.entities.Reservation;
import com.anoopkn.ticketsystem.repositories.EventRepository;
import com.anoopkn.ticketsystem.repositories.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlashSaleService {

    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;

    public FlashSaleService(EventRepository eventRepository, ReservationRepository reservationRepository) {
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public boolean reserveTicket(Long eventId, String userId){
        Event event = eventRepository.findById(eventId).orElseThrow(()->new RuntimeException("No event found"));
        if(event.getAvailableTickets() > 0){
            // Simulate a tiny bit of processing delay (e.g., payment check prep)
            try { Thread.sleep(50); } catch (InterruptedException e) { }

            event.setAvailableTickets(event.getAvailableTickets()-1);
            eventRepository.save(event);

            Reservation reservation = new Reservation(eventId, userId);
            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean  reserveTicketPessimistic(Long eventId, String userId) {
        // 1. Fetch the event WITH A DATABASE LOCK
        Event event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 2. The Check (Now protected by the lock)
        if (event.getAvailableTickets() > 0) {

            // Simulate network delay again
            try { Thread.sleep(50); } catch (InterruptedException e) { }

            // 3. The Mutation
            event.setAvailableTickets(event.getAvailableTickets() - 1);
            eventRepository.save(event);

            // 4. The Record
            Reservation reservation = new Reservation(eventId, userId);
            reservationRepository.save(reservation);

            return true;
        }

        return false;
    }
}
