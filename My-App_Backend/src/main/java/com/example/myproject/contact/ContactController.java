package com.example.myproject.contact;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    public record Result(String code, Map<String, String> fieldErrors) {}
    public record Status(boolean available) {}
    private final ContactService service;
    private final ContactRateLimiter limiter;
    private final ContactProperties config;
    public ContactController(ContactService service, ContactRateLimiter limiter, ContactProperties config) {
        this.service = service; this.limiter = limiter; this.config = config;
    }
    @GetMapping("/status") public ResponseEntity<Status> status() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new Status(service.available()));
    }
    @PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Result> send(@Valid @RequestBody ContactRequest data, HttpServletRequest request) {
        if (data.website() != null && !data.website().isBlank()) return result(400, "invalid_request");
        if (data.service() != null && !data.service().isEmpty() && !ContactService.SERVICES.containsKey(data.service()))
            return ResponseEntity.badRequest().body(new Result("invalid_request", Map.of("service", "invalid")));
        if (!service.available()) return result(503, "unavailable");
        String client = clientAddress(request);
        if (!limiter.acquire(client)) return ResponseEntity.status(429).header("Retry-After", String.valueOf(Math.max(1, limiter.retryAfterSeconds(client))))
            .body(new Result("rate_limited", Map.of()));
        try { service.send(data); return result(200, "accepted"); }
        catch (MailException failure) { return result(502, "send_failed"); }
    }
    String clientAddress(HttpServletRequest request) {
        String peer = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (!config.getTrustedProxies().contains(peer) || forwarded == null || forwarded.length() > 1024) return peer;
        String[] chain = forwarded.split(",");
        for (int i = chain.length - 1; i >= 0 && config.getTrustedProxies().contains(peer); i--) {
            String candidate = chain[i].trim();
            if (!candidate.matches("[0-9a-fA-F:.]{2,64}")) return request.getRemoteAddr();
            peer = candidate;
        }
        return peer;
    }
    private ResponseEntity<Result> result(int status, String code) {
        return ResponseEntity.status(status).cacheControl(CacheControl.noStore()).body(new Result(code, Map.of()));
    }
}
