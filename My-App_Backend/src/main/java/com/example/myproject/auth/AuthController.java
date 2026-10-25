package com.example.myproject.auth;

import com.example.myproject.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final AccountService accounts;
    public AuthController(AccountService accounts) { this.accounts = accounts; }
    @PostMapping("/api/login")
    public ResponseEntity<AuthDtos.LoginResult> login(@Valid @RequestBody AuthDtos.Login input, HttpServletRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(accounts.login(input, request.getRemoteAddr()));
    }
    @PostMapping("/api/register")
    public ResponseEntity<AuthDtos.Result> register(@Valid @RequestBody AuthDtos.Registration input, HttpServletRequest request) {
        accounts.register(input, request.getRemoteAddr());
        return ResponseEntity.status(202).cacheControl(CacheControl.noStore())
            .body(new AuthDtos.Result("registration_received", "Registration request processed. Try signing in with your credentials."));
    }
    @GetMapping("/api/account/{id}")
    public ResponseEntity<AuthDtos.AccountView> account(@PathVariable String id, Authentication authentication) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(accounts.ownAccount(authentication, id));
    }
    @GetMapping("/api/dashboard")
    public ResponseEntity<AuthDtos.AccountView> current(Authentication authentication) { return account(null, authentication); }
    @PostMapping("/api/logout")
    public ResponseEntity<AuthDtos.Result> logout() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(new AuthDtos.Result("local_logout", "Remove the token from this device. It remains valid until expiry."));
    }
}
