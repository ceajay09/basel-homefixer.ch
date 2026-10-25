package com.example.myproject.auth;

import com.example.myproject.model.Account;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

/** Pure preparation used by the explicit local tool and isolated tests. */
public final class AdminAccountSetup {
    private AdminAccountSetup() {}
    public static Account prepare(String mode, Account existing, String email, String password, PasswordEncoder encoder) {
        String normalized = AuthInput.email(email); AuthInput.newPassword(password);
        if ("create-admin".equals(mode)) {
            if (existing != null) throw new IllegalArgumentException("An account already exists; create-admin never promotes or overwrites it");
            Account account = new Account(); account.setID(UUID.randomUUID().toString());
            account.setEmail(normalized); account.setAdmin(true);
            account.setPassword(encoder.encode(password)); account.setAuthRevision(UUID.randomUUID().toString());
            return account;
        }
        if (!"reset-password".equals(mode) || existing == null) throw new IllegalArgumentException("Reset requires one existing account");
        // Preserve ID, profile and existing permission; never promote during password reset.
        existing.setPassword(encoder.encode(password)); existing.setAuthRevision(UUID.randomUUID().toString());
        return existing;
    }
}
