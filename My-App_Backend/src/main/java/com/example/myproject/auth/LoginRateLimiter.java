package com.example.myproject.auth;

import java.time.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Bounded, process-local limits. Ignore client-supplied forwarding headers. */
public final class LoginRateLimiter {
    private final Clock clock;
    private final Map<String, Deque<Instant>> clients = new HashMap<>();
    private final Map<String, Deque<Instant>> accounts = new HashMap<>();
    private final Deque<Instant> global = new ArrayDeque<>();
    private static final Duration WINDOW = Duration.ofMinutes(15);
    public LoginRateLimiter(Clock clock) { this.clock = clock; }
    private void prune(Deque<Instant> queue, Instant cutoff) {
        while (!queue.isEmpty() && !queue.peekFirst().isAfter(cutoff)) queue.removeFirst();
    }
    private void prune(Map<String, Deque<Instant>> entries, Instant cutoff) {
        entries.values().forEach(queue -> prune(queue, cutoff));
        entries.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
    public synchronized long acquire(String client, String email) {
        Instant now = clock.instant(), cutoff = now.minus(WINDOW);
        prune(clients, cutoff); prune(accounts, cutoff); prune(global, cutoff);
        String account = digest(email);
        Deque<Instant> ipAttempts = clients.get(client), accountAttempts = accounts.get(account);
        Instant retry = null;
        if (global.size() >= 100) retry = global.peekFirst().plus(WINDOW);
        if (ipAttempts != null && ipAttempts.size() >= 20) retry = later(retry, ipAttempts.peekFirst().plus(WINDOW));
        if (accountAttempts != null && accountAttempts.size() >= 5) retry = later(retry, accountAttempts.peekFirst().plus(WINDOW));
        if ((!clients.containsKey(client) && clients.size() >= 2048) || (!accounts.containsKey(account) && accounts.size() >= 2048)) retry = later(retry, now.plusSeconds(60));
        if (retry != null) return Math.max(1, Duration.between(now, retry).getSeconds() + 1);
        clients.computeIfAbsent(client, ignored -> new ArrayDeque<>()).addLast(now);
        accounts.computeIfAbsent(account, ignored -> new ArrayDeque<>()).addLast(now);
        global.addLast(now);
        return 0;
    }
    private Instant later(Instant first, Instant second) { return first == null || second.isAfter(first) ? second : first; }
    private String digest(String value) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException failure) { throw new IllegalStateException(failure); }
    }
}
