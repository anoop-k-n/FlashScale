package com.anoopkn.ticketsystem.services;

import com.anoopkn.ticketsystem.entities.Reservation;
import com.anoopkn.ticketsystem.entities.TicketReservedEvent;
import com.anoopkn.ticketsystem.repositories.ReservationRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ReservationEventConsumer {

    private final ReservationRepository reservationRepository;
    private final ObjectMapper objectMapper;

    public ReservationEventConsumer(ReservationRepository reservationRepository, ObjectMapper objectMapper) {
        this.reservationRepository = reservationRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "ticket-reservations", groupId = "tickets-group")
    public void consume(String message) {
        try {
            // 1. Deserialize the incoming JSON from Kafka
            TicketReservedEvent event = objectMapper.readValue(message, TicketReservedEvent.class);

            // 2. Create the entity using your custom constructor
            // This ensures LocalDateTime.now() is triggered inside the constructor
            Reservation reservation = new Reservation(event.eventId(), event.userId());

            // 3. Save to PostgreSQL
            reservationRepository.save(reservation);

            System.out.println("✅ Postgres Updated: User " + event.userId() + " for Event " + event.eventId());

        } catch (Exception e) {
            System.err.println("❌ Failed to persist reservation: " + e.getMessage());
        }
    }
}