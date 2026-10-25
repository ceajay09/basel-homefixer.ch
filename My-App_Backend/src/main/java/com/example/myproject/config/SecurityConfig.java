package com.example.myproject.config;

import com.example.myproject.auth.*;
import com.example.myproject.contact.ContactConfiguration;
import com.example.myproject.content.ContentConfiguration;
import com.example.myproject.repository.AccountRepository;
import com.example.myproject.service.TokenService;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Import(AuthenticationConfiguration.class)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TokenService tokens,
            ObjectProvider<AccountRepository> repositories) throws Exception {
        return http
            // Authentication is exclusively an explicit Authorization header, not a browser cookie or Basic auth.
            .csrf(csrf -> csrf.disable())
            .cors(withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable())
            .httpBasic(basic -> basic.disable())
            .addFilterBefore(new BearerAuthenticationFilter(tokens, repositories), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((request, response, failure) -> AuthResponses.unauthorized(response))
                .accessDeniedHandler((request, response, failure) -> AuthResponses.write(response, 403, "forbidden")))
            .authorizeHttpRequests(auth -> auth
                // Preserve controller 400/404 responses during Spring Boot error dispatch.
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers("/api/admin", "/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/login", "/api/register").permitAll()
                .requestMatchers("/api/login", "/api/register").denyAll()
                .requestMatchers(HttpMethod.POST, "/api/logout").authenticated()
                .requestMatchers("/api/logout").denyAll()
                .requestMatchers(HttpMethod.GET, "/api/account/**", "/api/dashboard").authenticated()
                .requestMatchers(HttpMethod.HEAD, "/api/account/**", "/api/dashboard").authenticated()
                .requestMatchers("/api/account/**", "/api/dashboard").denyAll()
                .requestMatchers(HttpMethod.GET, "/api/projects/**", "/api/blog/**", "/api/media/**", "/api/content/**").permitAll()
                .requestMatchers(HttpMethod.HEAD, "/api/projects/**", "/api/blog/**", "/api/media/**", "/api/content/**").permitAll()
                .requestMatchers("/api/projects/**", "/api/blog/**", "/api/media/**", "/api/content/**").denyAll()
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration legacy = new CorsConfiguration();
        legacy.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000", "https://basel-homefixer.ch"));
        legacy.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        legacy.setAllowedHeaders(List.of("*"));
        legacy.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/contact", ContactConfiguration.cors());
        source.registerCorsConfiguration("/api/contact/status", ContactConfiguration.cors());
        for (String path : ContentConfiguration.PATHS) source.registerCorsConfiguration(path, ContentConfiguration.cors());
        for (String path : AuthenticationConfiguration.PATHS) source.registerCorsConfiguration(path, AuthenticationConfiguration.cors());
        source.registerCorsConfiguration("/**", legacy);
        return source;
    }
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
}
