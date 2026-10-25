package com.example.myproject.contact;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Bound JSON before deserialisation, including requests without Content-Length. */
@Component
@Order(-100)
public class ContactBodyLimitFilter extends OncePerRequestFilter {
    @Override protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getMethod().equals("POST") || !request.getRequestURI().equals(request.getContextPath() + "/api/contact");
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        byte[] bytes = request.getInputStream().readNBytes(32769);
        if (bytes.length > 32768) {
            response.setStatus(413); response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"invalid_request\",\"fieldErrors\":{}}"); return;
        }
        chain.doFilter(new HttpServletRequestWrapper(request) {
            @Override public ServletInputStream getInputStream() {
                ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                return new ServletInputStream() {
                    @Override public int read() { return input.read(); }
                    @Override public boolean isFinished() { return input.available() == 0; }
                    @Override public boolean isReady() { return true; }
                    @Override public void setReadListener(ReadListener listener) { throw new UnsupportedOperationException("Synchronous contact requests only"); }
                };
            }
            @Override public BufferedReader getReader() { return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8)); }
        }, response);
    }
}
