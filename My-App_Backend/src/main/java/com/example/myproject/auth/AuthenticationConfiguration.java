package com.example.myproject.auth;

import com.example.myproject.content.ContentConfiguration;
import com.example.myproject.service.TokenService;
import java.time.Clock;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration(proxyBeanMethods = false)
public class AuthenticationConfiguration implements WebMvcConfigurer {
    public static final List<String> PATHS = List.of("/api/login", "/api/register", "/api/logout", "/api/dashboard", "/api/account/**", "/api/admin/**");
    public static CorsConfiguration cors() {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(ContentConfiguration.ORIGINS);
        cors.setAllowedMethods(List.of("GET", "HEAD", "POST", "PUT", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Content-Type", "Authorization", "If-Match"));
        cors.setAllowCredentials(false);
        cors.setExposedHeaders(List.of("ETag", "Location", "Retry-After"));
        return cors;
    }
    @Override public void addCorsMappings(CorsRegistry registry) {
        for (String path : PATHS) registry.addMapping(path)
            .allowedOrigins(ContentConfiguration.ORIGINS.toArray(String[]::new))
            .allowedMethods("GET", "HEAD", "POST", "PUT", "OPTIONS").allowedHeaders("Content-Type", "Authorization", "If-Match").exposedHeaders("ETag", "Location", "Retry-After").allowCredentials(false);
    }
    @Bean public AuthSettings authSettings(
        @Value("${AUTH_JWT_SECRET_BASE64:}") String secret,
        @Value("${AUTH_TOKEN_TTL_SECONDS:900}") long ttl,
        @Value("${AUTH_PUBLIC_REGISTRATION_ENABLED:false}") boolean registration) {
        return new AuthSettings(secret, ttl, registration);
    }
    @Bean public TokenService tokenService(AuthSettings settings) { return new TokenService(settings, Clock.systemUTC()); }
    @Bean public LoginRateLimiter loginRateLimiter() { return new LoginRateLimiter(Clock.systemUTC()); }
    // Prevent an unrelated generated Basic-auth user/password. Authentication uses Bearer only.
    @Bean public UserDetailsService noPasswordAuthenticationProvider() {
        return username -> { throw new UsernameNotFoundException("Basic authentication is disabled"); };
    }
}
