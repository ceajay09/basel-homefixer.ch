package com.example.myproject.auth;

import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;

public final class AuthResponses {
    private AuthResponses() {}
    public static void unauthorized(HttpServletResponse response) throws IOException {
        response.setHeader("WWW-Authenticate", "Bearer");
        write(response, 401, "unauthorized");
    }
    public static void write(HttpServletResponse response, int status, String code) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write("{\"code\":\"" + code + "\"}");
    }
}
