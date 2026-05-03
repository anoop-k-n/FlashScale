package com.anoopkn.ticketsystem.controllers;

import com.anoopkn.ticketsystem.services.FlashSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    public FlashSaleController(FlashSaleService flashSaleService){
        this.flashSaleService = flashSaleService;
    }

    @PostMapping("/{eventId}/reserve")
    public boolean reserve(@PathVariable Long eventId, @RequestParam String userId){
        return flashSaleService.reserveTicket(eventId, userId);
    }

}
