-- KEYS[1] = The Redis key for the event inventory (e.g., "event:1:inventory")

local inventory = tonumber(redis.call('GET', KEYS[1]))

-- If the key doesn't exist or inventory is 0, fail (return 0)
if inventory == nil or inventory <= 0 then
    return 0
end

-- Otherwise, decrement the inventory by 1
redis.call('DECR', KEYS[1])

-- Return 1 for success
return 1