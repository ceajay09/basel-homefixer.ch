package com.example.myproject.auth;

import com.example.myproject.model.Account;
import com.example.myproject.service.TokenService;
import java.time.*;
import java.util.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {
    static final String SECRET = Base64.getEncoder().encodeToString(new byte[32]); // Test fixture only.
    static final Instant NOW = Instant.parse("2026-10-24T12:00:00Z");
    static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    static Account account() {
        var account = new Account(); account.setID("isolated-id"); account.setEmail("test@example.com");
        account.setPassword(new BCryptPasswordEncoder(4).encode("isolated-password"));
        account.setAuthRevision(UUID.randomUUID().toString()); return account;
    }
    TokenService service(Clock clock) { return new TokenService(new AuthSettings(SECRET, 900, false), clock); }
    @Test void stableConfiguredKeyVerifiesAcrossInstancesWithoutPrivateClaims() {
        Account account = account(); String token = service(CLOCK).issue(account).token();
        var identity = service(CLOCK).verify(token).orElseThrow();
        assertEquals(account.getID(), identity.accountId()); assertEquals(account.getAuthRevision(), identity.revision());
        String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), java.nio.charset.StandardCharsets.UTF_8);
        assertFalse(payload.contains("password")); assertFalse(payload.contains("test@example.com")); assertFalse(payload.contains("admin"));
    }
    @Test void expiredTamperedAndWrongKeyTokensAreRejected() {
        String token = service(CLOCK).issue(account()).token();
        assertTrue(service(Clock.fixed(NOW.plusSeconds(901), ZoneOffset.UTC)).verify(token).isEmpty());
        assertTrue(service(CLOCK).verify(token.substring(0, token.lastIndexOf('.')+1) + "not-a-valid-signature").isEmpty());
        byte[] other = new byte[32]; Arrays.fill(other,(byte)1);
        assertTrue(new TokenService(new AuthSettings(Base64.getEncoder().encodeToString(other),900,false), CLOCK).verify(token).isEmpty());
    }
    @Test void unsignedMissingExpiryWrongAudienceAndFutureIssuedTokensAreRejected() {
        var key = Keys.hmacShaKeyFor(new byte[32]);
        for (String token : List.of(
            Jwts.builder().subject("x").compact(),
            Jwts.builder().issuer(TokenService.ISSUER).audience().add(TokenService.AUDIENCE).and().subject("x").claim("rev",UUID.randomUUID().toString()).issuedAt(Date.from(NOW)).signWith(key).compact(),
            Jwts.builder().issuer(TokenService.ISSUER).audience().add("other").and().subject("x").claim("rev",UUID.randomUUID().toString()).issuedAt(Date.from(NOW)).expiration(Date.from(NOW.plusSeconds(100))).signWith(key).compact(),
            Jwts.builder().issuer(TokenService.ISSUER).audience().add(TokenService.AUDIENCE).and().subject("x").claim("rev",UUID.randomUUID().toString()).issuedAt(Date.from(NOW.plusSeconds(10))).expiration(Date.from(NOW.plusSeconds(100))).signWith(key).compact())) {
            assertTrue(service(CLOCK).verify(token).isEmpty());
        }
    }
    @Test void missingKeyDisablesAuthenticationAndInvalidKeyFailsClearly() {
        var unavailable = new TokenService(new AuthSettings("",900,false),CLOCK);
        assertFalse(unavailable.available()); assertTrue(unavailable.verify("anything").isEmpty());
        assertThrows(IllegalArgumentException.class,()->new TokenService(new AuthSettings("not-base64",900,false), CLOCK));
        assertThrows(IllegalArgumentException.class,()->new TokenService(new AuthSettings(Base64.getEncoder().encodeToString(new byte[16]),900,false), CLOCK));
        assertThrows(IllegalArgumentException.class,()->new AuthSettings(SECRET,86400,false));
    }
}
