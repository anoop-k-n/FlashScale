package com.anoopkn.ticketsystem.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@jakarta.persistence.Entity
@Getter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    private int totalTickets;

    @Setter
    private int availableTickets;

    // constructors
    public Event(){};

    public Event(String name, int totalTickets){
        this.name = name;
        this.totalTickets = totalTickets;
        this.availableTickets = totalTickets;
    }

}
