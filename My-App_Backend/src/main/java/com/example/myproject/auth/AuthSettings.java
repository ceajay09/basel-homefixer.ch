package com.example.myproject.auth;

/** No secret is ever included in diagnostics or JSON responses. */
public record AuthSettings(String secretBase64, long tokenTtlSeconds, boolean publicRegistrationEnabled) {
    public AuthSettings {
        if (tokenTtlSeconds < 60 || tokenTtlSeconds > 3600) {
            throw new IllegalArgumentException("AUTH_TOKEN_TTL_SECONDS must be between 60 and 3600");
        }
    }
    @Override public String toString() { return "AuthSettings[secret=REDACTED, ttl=" + tokenTtlSeconds + "]"; }
}
