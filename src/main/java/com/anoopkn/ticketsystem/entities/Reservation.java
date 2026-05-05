package com.anoopkn.ticketsystem.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Long eventId;

    @Setter
    private String reservedBy;

    @Setter
    private LocalDateTime reservationTime;


    // constructors
    public Reservation(){};

    public Reservation(Long eventId, String reservedBy){
        this.eventId = eventId;
        this.reservedBy = reservedBy;
        this.reservationTime = LocalDateTime.now();
    };
}
