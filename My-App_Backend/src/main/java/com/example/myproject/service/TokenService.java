package com.example.myproject.service;

import com.example.myproject.auth.*;
import com.example.myproject.model.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.time.*;
import java.util.*;

/** Single signing and verification implementation; no generated restart-dependent keys. */
public final class TokenService {
    public static final String ISSUER = "basel-homefixer.ch", AUDIENCE = "website-api";
    private final AuthSettings settings;
    private final Clock clock;
    private final SecretKey key;
    public record Issued(String token, Instant expiresAt, long expiresIn) { @Override public String toString() { return "Issued[token=REDACTED]"; } }
    public record Identity(String accountId, String revision) {}
    public TokenService(AuthSettings settings, Clock clock) {
        this.settings = settings; this.clock = clock;
        if (settings.secretBase64() == null || settings.secretBase64().isBlank()) { key = null; return; }
        try {
            byte[] decoded = Base64.getDecoder().decode(settings.secretBase64());
            if (decoded.length != 32) throw new IllegalArgumentException();
            key = Keys.hmacShaKeyFor(decoded);
            Arrays.fill(decoded, (byte)0);
        } catch (IllegalArgumentException failure) {
            throw new IllegalArgumentException("AUTH_JWT_SECRET_BASE64 must encode exactly 32 random bytes");
        }
    }
    public boolean available() { return key != null; }
    public Issued issue(Account account) {
        if (!available() || !AuthInput.usable(account)) throw new IllegalStateException("Authentication unavailable");
        Instant issued = clock.instant().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        Instant expires = issued.plusSeconds(settings.tokenTtlSeconds());
        String token = Jwts.builder().issuer(ISSUER).audience().add(AUDIENCE).and()
            .subject(account.getID()).claim("rev", account.getAuthRevision())
            .issuedAt(Date.from(issued)).expiration(Date.from(expires))
            .signWith(key, Jwts.SIG.HS256).compact();
        return new Issued(token, expires, settings.tokenTtlSeconds());
    }
    public Optional<Identity> verify(String token) {
        if (!available() || token == null || token.length() > 4096) return Optional.empty();
        try {
            var signed = Jwts.parser().verifyWith(key).requireIssuer(ISSUER).requireAudience(AUDIENCE)
                .clock(() -> Date.from(clock.instant())).sig().clear().add(Jwts.SIG.HS256).and()
                .build().parseSignedClaims(token);
            Claims claims = signed.getPayload();
            String subject = claims.getSubject(), revision = claims.get("rev", String.class);
            Date issued = claims.getIssuedAt(), expires = claims.getExpiration();
            if (subject == null || subject.isBlank() || subject.length() > 128 || revision == null || issued == null || expires == null
                || issued.toInstant().isAfter(clock.instant()) || !expires.after(issued)
                || expires.toInstant().isAfter(issued.toInstant().plusSeconds(settings.tokenTtlSeconds()))) return Optional.empty();
            UUID.fromString(revision);
            return Optional.of(new Identity(subject, revision));
        } catch (JwtException | IllegalArgumentException ignored) { return Optional.empty(); }
    }
}
