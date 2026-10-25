package com.example.myproject.auth;

public final class AuthFailure extends RuntimeException {
    private final int status;
    private final String code;
    private final long retryAfter;
    public AuthFailure(int status, String code, String message) { this(status, code, message, 0); }
    public AuthFailure(int status, String code, String message, long retryAfter) {
        super(message); this.status = status; this.code = code; this.retryAfter = retryAfter;
    }
    public int status() { return status; }
    public String code() { return code; }
    public long retryAfter() { return retryAfter; }
}
