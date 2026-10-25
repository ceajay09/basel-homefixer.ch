package com.example.myproject.auth;

import com.example.myproject.model.Account;
import com.example.myproject.repository.AccountRepository;
import com.example.myproject.service.TokenService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/** Only installed inside Spring Security; never registered as a separate servlet filter. */
public final class BearerAuthenticationFilter extends OncePerRequestFilter {
    public record AccountPrincipal(String id) implements Principal {
        @Override public String getName() { return id; }
    }
    private final TokenService tokens;
    private final ObjectProvider<AccountRepository> repositories;
    public BearerAuthenticationFilter(TokenService tokens, ObjectProvider<AccountRepository> repositories) {
        this.tokens = tokens; this.repositories = repositories;
    }
    @Override protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !(path.equals("/api/admin") || path.startsWith("/api/admin/") || path.equals("/api/account")
            || path.startsWith("/api/account/") || path.equals("/api/dashboard") || path.equals("/api/logout")
            || path.startsWith("/actuator/") && !path.equals("/actuator/health") && !path.equals("/actuator/info"));
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        String authorization = request.getHeader("Authorization");
        if (authorization == null) { chain.doFilter(request, response); return; }
        if (!authorization.regionMatches(true, 0, "Bearer ", 0, 7)) { AuthResponses.unauthorized(response); return; }
        var identity = tokens.verify(authorization.substring(7));
        if (identity.isEmpty()) { AuthResponses.unauthorized(response); return; }
        try {
            AccountRepository repository = repositories.getIfAvailable();
            if (repository == null) { AuthResponses.write(response, 503, "authentication_unavailable"); return; }
            Account account = repository.findById(identity.get().accountId()).orElse(null);
            if (!AuthInput.usable(account) || !account.getAuthRevision().equals(identity.get().revision())) {
                AuthResponses.unauthorized(response); return;
            }
            var authorities = List.of(new SimpleGrantedAuthority(account.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER"));
            var authentication = UsernamePasswordAuthenticationToken.authenticated(new AccountPrincipal(account.getID()), null, authorities);
            var context = SecurityContextHolder.createEmptyContext(); context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        } catch (DataAccessException failure) {
            AuthResponses.write(response, 503, "authentication_unavailable"); return;
        }
        chain.doFilter(request, response);
    }
}
