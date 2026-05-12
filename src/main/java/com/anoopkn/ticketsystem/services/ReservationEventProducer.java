package com.anoopkn.ticketsystem.services;

import com.anoopkn.ticketsystem.entities.TicketReservedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReservationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "ticket-reservations";

    public ReservationEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendReservationEvent(Long eventId, String userId) {
        TicketReservedEvent event = new TicketReservedEvent(eventId, userId);

        // Send to topic, using eventId as the partition key, and the record as the payload
        kafkaTemplate.send(TOPIC, String.valueOf(eventId), event);
    }
}