package com.example.myproject.util;

import com.example.myproject.auth.AuthInput;
import com.example.myproject.model.Account;
import com.example.myproject.repository.AccountRepository;
import com.example.myproject.service.TokenService;
import org.springframework.stereotype.Component;

/** Compatibility adapter; verification always delegates to the single TokenService. */
@Component
public final class TokenUtil {
    private final AccountRepository accounts;
    private final TokenService tokens;
    public TokenUtil(AccountRepository accounts, TokenService tokens) { this.accounts = accounts; this.tokens = tokens; }
    public Account getAccountFromToken(String authorization) {
        if (authorization == null) return null;
        String token = authorization.regionMatches(true, 0, "Bearer ", 0, 7) ? authorization.substring(7) : authorization;
        var identity = tokens.verify(token);
        if (identity.isEmpty()) return null;
        Account account = accounts.findById(identity.get().accountId()).orElse(null);
        return AuthInput.usable(account) && account.getAuthRevision().equals(identity.get().revision()) ? account : null;
    }
}
