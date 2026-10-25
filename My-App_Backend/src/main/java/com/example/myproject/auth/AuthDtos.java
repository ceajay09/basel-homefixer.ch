package com.example.myproject.auth;

import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() {}
    public record Login(@NotBlank @Size(max=254) String email, @NotBlank @Size(max=72) String password) {
        @Override public String toString() { return "Login[credentials=REDACTED]"; }
    }
    public record Registration(@NotBlank @Size(max=254) String email, @NotBlank @Size(min=12,max=72) String password,
        @Size(max=100) String firstName, @Size(max=100) String lastName,
        @Size(max=200) String company, @Size(max=50) String phoneNumber) {
        @Override public String toString() { return "Registration[credentials=REDACTED]"; }
    }
    public record AccountView(String id, String email, String firstName, String lastName, boolean admin) {}
    public record LoginResult(String token, String tokenType, long expiresIn, java.time.Instant expiresAt, AccountView account) {
        @Override public String toString() { return "LoginResult[token=REDACTED]"; }
    }
    public record Result(String code, String message) {}
}
