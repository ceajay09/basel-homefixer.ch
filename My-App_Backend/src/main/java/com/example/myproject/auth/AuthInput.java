package com.example.myproject.auth;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import com.example.myproject.model.Account;

public final class AuthInput {
    private AuthInput() {}
    private static final Pattern HASH = Pattern.compile("^\\$2[aby]\\$(0[4-9]|1[0-6])\\$[./A-Za-z0-9]{53}$");
    public static String email(String value) {
        if (value == null || value.length() > 254 || !value.trim().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new AuthFailure(400, "invalid_request", "Please provide a valid email address.");
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
    public static boolean passwordFits(String value) {
        return value != null && !value.isBlank() && value.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
    public static void newPassword(String value) {
        if (!passwordFits(value) || value.length() < 12) {
            throw new AuthFailure(400, "invalid_password", "Use at least 12 characters and at most 72 UTF-8 bytes.");
        }
    }
    public static boolean usable(Account account) {
        if (account == null || account.getID() == null || account.getPassword() == null || !HASH.matcher(account.getPassword()).matches()) return false;
        try { return UUID.fromString(account.getAuthRevision()).toString().equals(account.getAuthRevision()); }
        catch (IllegalArgumentException | NullPointerException ignored) { return false; }
    }
}
