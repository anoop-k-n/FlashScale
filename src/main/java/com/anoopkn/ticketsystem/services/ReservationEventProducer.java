package com.anoopkn.ticketsystem.services;

import com.anoopkn.ticketsystem.entities.TicketReservedEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class ReservationEventProducer {

    // Notice we changed this from <String, Object> to <String, String>
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "ticket-reservations";

    // Inject the ObjectMapper alongside the KafkaTemplate
    public ReservationEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                    ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendReservationEvent(Long eventId, String userId) {
        TicketReservedEvent event = new TicketReservedEvent(eventId, userId);
        try {
            String jsonPayload = objectMapper.writeValueAsString(event);

            // Add a callback to see if the broker actually accepted the message
            kafkaTemplate.send(TOPIC, String.valueOf(eventId), jsonPayload)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("DEBUG: Message sent to Kafka! Offset: " +
                                    result.getRecordMetadata().offset());
                        } else {
                            System.err.println("DEBUG: Kafka Send Failed: " + ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize", e);
        }
    }
}