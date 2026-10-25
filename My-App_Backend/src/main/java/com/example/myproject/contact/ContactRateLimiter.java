package com.example.myproject.contact;

import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

/** In-memory attempt limits per backend process: 3/15min per address, 20/hour globally. */
@Component
public class ContactRateLimiter {
    private final Clock clock;
    private final Map<String, Deque<Instant>> clients = new HashMap<>();
    private final Deque<Instant> global = new ArrayDeque<>();
    public ContactRateLimiter() { this(Clock.systemUTC()); }
    ContactRateLimiter(Clock clock) { this.clock = clock; }
    private void prune(Deque<Instant> queue, Instant cutoff) {
        while (!queue.isEmpty() && !queue.peekFirst().isAfter(cutoff)) queue.removeFirst();
    }
    public synchronized long retryAfterSeconds(String client) {
        Instant until = global.size() >= 20 ? global.peekFirst().plusSeconds(3600)
            : clients.containsKey(client) && !clients.get(client).isEmpty() ? clients.get(client).peekFirst().plusSeconds(900) : clock.instant().plusSeconds(900);
        return Math.max(1, Duration.between(clock.instant(), until).getSeconds() + 1);
    }
    public synchronized boolean acquire(String client) {
        Instant now = clock.instant();
        clients.values().forEach(queue -> prune(queue, now.minusSeconds(900)));
        clients.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        prune(global, now.minusSeconds(3600));
        if (global.size() >= 20 || (!clients.containsKey(client) && clients.size() >= 1024)) return false;
        Deque<Instant> queue = clients.computeIfAbsent(client, ignored -> new ArrayDeque<>());
        if (queue.size() >= 3) return false;
        queue.addLast(now); global.addLast(now); return true;
    }
}
