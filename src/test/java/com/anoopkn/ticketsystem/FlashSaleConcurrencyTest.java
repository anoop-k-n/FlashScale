package com.anoopkn.ticketsystem;

import com.anoopkn.ticketsystem.entities.Event;
import com.anoopkn.ticketsystem.repositories.EventRepository;
import com.anoopkn.ticketsystem.repositories.ReservationRepository;
import com.anoopkn.ticketsystem.services.FlashSaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class FlashSaleConcurrencyTest {

    @Autowired
    private FlashSaleService flashSaleService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void proveRaceCondition() throws InterruptedException {
        reservationRepository.deleteAll();
        eventRepository.deleteAll();


        // 1. Setup: Create an event with EXACTLY 10 tickets
        Event event = new Event("Concurrency Concert", 10);
        eventRepository.save(event);
        Long eventId = event.getId();

        // 2. Concurrency Setup: 100 users trying to buy at the exact same time
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // The "starting gun" - makes all threads wait until we say GO
        CountDownLatch startLatch = new CountDownLatch(1);
        // Tracks when all 100 threads are completely finished
        CountDownLatch completionLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successfulReservations = new AtomicInteger(0);

        // 3. Queue up the 100 threads
        for (int i = 0; i < numberOfThreads; i++) {
            String userId = "User_" + i;
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Thread pauses here, waiting for the gun

                    boolean response = flashSaleService.reserveTicketPessimistic(eventId, userId); // race condition if just reserveTicket is used
                    if (response) {
                        successfulReservations.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completionLatch.countDown(); // Thread finished
                }
            });
        }

        System.out.println("All threads queued. Firing the starting gun...");

        // 4. FIRE! This releases all 100 threads simultaneously
        startLatch.countDown();

        // 5. Wait for the dust to settle
        completionLatch.await();
        executorService.shutdown();

        // 6. The Moment of Truth: Check the Database
        Event updatedEvent = eventRepository.findById(eventId).orElseThrow();
        long actualReservationsInDb = reservationRepository.count();

        System.out.println("--- TEST RESULTS ---");
        System.out.println("Initial Tickets: 10");
        System.out.println("Total Requests: " + numberOfThreads);
        System.out.println("Tickets Remaining in DB: " + updatedEvent.getAvailableTickets());
        System.out.println("Success Responses Sent: " + successfulReservations.get());
        System.out.println("Actual Reservation Records in DB: " + actualReservationsInDb);

        // If the system was safe, Success Responses and DB Records would be exactly 10.
    }
}