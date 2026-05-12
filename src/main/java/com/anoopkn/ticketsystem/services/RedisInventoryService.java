package com.anoopkn.ticketsystem.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class RedisInventoryService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> reserveScript;

    public RedisInventoryService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        // Initialize the script once at startup to avoid parsing overhead during the flash sale
        this.reserveScript = new DefaultRedisScript<>();
        this.reserveScript.setLocation(new ClassPathResource("scripts/reserve_inventory.lua"));
        this.reserveScript.setResultType(Long.class);
    }

    public boolean reserveTicketAtomically(Long eventId) {
        String redisKey = "event:" + eventId + ":inventory";

        // Execute the script. Collections.singletonList maps to KEYS[1] in the Lua script.
        Long result = redisTemplate.execute(
                reserveScript,
                Collections.singletonList(redisKey)
        );

        // A result of 1 means the decrement was successful
        return result != null && result == 1L;
    }

    // Helper method to seed the data before we test
    public void seedInventory(Long eventId, int totalTickets) {
        redisTemplate.opsForValue().set("event:" + eventId + ":inventory", String.valueOf(totalTickets));
    }
}