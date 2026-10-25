package com.example.myproject.service;

import com.example.myproject.auth.*;
import com.example.myproject.model.Account;
import com.example.myproject.repository.AccountRepository;
import java.util.*;
import org.springframework.dao.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** No plaintext fallback and no privilege assignment from public input. */
@Service
public class AccountService {
    private final AccountRepository accounts;
    private final TokenService tokens;
    private final PasswordEncoder passwords;
    private final AuthSettings settings;
    private final LoginRateLimiter limiter;
    private final AuthAccountIndexes indexes;
    private final String dummyHash;
    public AccountService(AccountRepository accounts, TokenService tokens, PasswordEncoder passwords,
        AuthSettings settings, LoginRateLimiter limiter, AuthAccountIndexes indexes) {
        this.accounts = accounts; this.tokens = tokens; this.passwords = passwords;
        this.settings = settings; this.limiter = limiter; this.indexes = indexes;
        dummyHash = passwords.encode(UUID.randomUUID().toString());
    }
    private void available() {
        if (!tokens.available()) throw new AuthFailure(503, "authentication_unavailable", "Sign-in is currently unavailable.");
    }
    private void limit(String client, String email) {
        long retry = limiter.acquire(client, email);
        if (retry > 0) throw new AuthFailure(429, "rate_limited", "Too many attempts. Please try again later.", retry);
    }
    public AuthDtos.LoginResult login(AuthDtos.Login input, String client) {
        available();
        String email = AuthInput.email(input.email());
        limit(client, email);
        if (!AuthInput.passwordFits(input.password())) throw new AuthFailure(400, "invalid_request", "Please check your sign-in details.");
        List<Account> found = accounts.findByEmailIgnoreCase(email);
        Account account = found.size() == 1 ? found.get(0) : null;
        boolean usable = AuthInput.usable(account);
        boolean matches = passwords.matches(input.password(), usable ? account.getPassword() : dummyHash);
        if (!usable || !matches) throw new AuthFailure(401, "invalid_credentials", "Email or password is incorrect.");
        var issued = tokens.issue(account);
        return new AuthDtos.LoginResult(issued.token(), "Bearer", issued.expiresIn(), issued.expiresAt(), view(account));
    }
    public void register(AuthDtos.Registration input, String client) {
        if (!settings.publicRegistrationEnabled()) throw new AuthFailure(403, "registration_disabled", "Public registration is disabled.");
        available();
        String email = AuthInput.email(input.email());
        limit(client, "register:" + email); AuthInput.newPassword(input.password());
        if (!indexes.registrationReady()) throw new AuthFailure(503, "registration_unavailable", "Registration is currently unavailable.");
        if (!accounts.findByEmailIgnoreCase(email).isEmpty()) return; // Same response, no email-existence disclosure.
        Account account = new Account();
        account.setEmail(email); account.setPassword(passwords.encode(input.password()));
        account.setAuthRevision(UUID.randomUUID().toString()); account.setAdmin(false);
        account.setFirstName(input.firstName()); account.setLastName(input.lastName());
        account.setCompany(input.company()); account.setPhoneNumber(input.phoneNumber());
        try { accounts.insert(account); } catch (DuplicateKeyException concurrentRegistration) { /* Same neutral response. */ }
    }
    public AuthDtos.AccountView ownAccount(Authentication authentication, String requestedId) {
        String id = authentication.getName();
        if (requestedId != null && !id.equals(requestedId)) throw new AuthFailure(403, "forbidden", "This account is not accessible.");
        Account account = accounts.findById(id).orElseThrow(() -> new AuthFailure(401, "unauthorized", "Please sign in again."));
        return view(account);
    }
    private AuthDtos.AccountView view(Account account) {
        return new AuthDtos.AccountView(account.getID(), account.getEmail(), account.getFirstName(), account.getLastName(), account.isAdmin());
    }
}
