package com.example.myproject.auth;

import com.example.myproject.model.Account;
import com.example.myproject.repository.AccountRepository;
import com.example.myproject.service.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class AccountServiceTest {
    final AccountRepository repository=mock(AccountRepository.class);
    final AuthAccountIndexes indexes=mock(AuthAccountIndexes.class);
    final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(4);
    AccountService service(boolean registration) {
        var settings=new AuthSettings(TokenServiceTest.SECRET,900,registration);
        return new AccountService(repository,new TokenService(settings,TokenServiceTest.CLOCK),encoder,settings,new LoginRateLimiter(TokenServiceTest.CLOCK),indexes);
    }
    @Test void registrationHashesPasswordsAndNeverGrantsAdmin() {
        when(indexes.registrationReady()).thenReturn(true);when(repository.findByEmailIgnoreCase(anyString())).thenReturn(List.of());
        service(true).register(new AuthDtos.Registration(" Person@Example.com ","a-long-test-password",null,null,null,null),"peer");
        var account=org.mockito.ArgumentCaptor.forClass(Account.class);verify(repository).insert(account.capture());
        assertEquals("person@example.com",account.getValue().getEmail());assertFalse(account.getValue().isAdmin());
        assertNotEquals("a-long-test-password",account.getValue().getPassword());assertTrue(encoder.matches("a-long-test-password",account.getValue().getPassword()));
        assertNotNull(account.getValue().getAuthRevision());
    }
    @Test void legacyPlaintextAndUnmanagedHashedAccountsCannotSignInOrBeUpgradedAutomatically() {
        for(String stored:List.of("legacy-password",encoder.encode("legacy-password"))) {
            var account=new Account();account.setID("legacy");account.setPassword(stored);account.setAdmin(true);
            when(repository.findByEmailIgnoreCase("old@example.com")).thenReturn(List.of(account));
            var failure=assertThrows(AuthFailure.class,()->service(false).login(new AuthDtos.Login("old@example.com","legacy-password"),"peer"));
            assertEquals(401,failure.status());assertEquals(stored,account.getPassword());assertNull(account.getAuthRevision());
        }
        verify(repository,never()).save(any(Account.class));verify(repository,never()).insert(any(Account.class));
    }
    @Test void wrongPasswordAndUnknownEmailHaveIdenticalFailure() {
        Account account=TokenServiceTest.account();when(repository.findByEmailIgnoreCase("known@example.com")).thenReturn(List.of(account));when(repository.findByEmailIgnoreCase("missing@example.com")).thenReturn(List.of());
        var service=service(false);
        var known=assertThrows(AuthFailure.class,()->service.login(new AuthDtos.Login("known@example.com","incorrect-password"),"peer"));
        var missing=assertThrows(AuthFailure.class,()->service.login(new AuthDtos.Login("missing@example.com","incorrect-password"),"peer"));
        assertEquals(known.code(),missing.code());assertEquals(known.getMessage(),missing.getMessage());
    }
    @Test void publicRegistrationIsOffByDefaultAndRequiresVerifiedDatabaseIndex() {
        var input=new AuthDtos.Registration("test@example.com","a-long-test-password",null,null,null,null);
        assertEquals(403,assertThrows(AuthFailure.class,()->service(false).register(input,"peer")).status());
        assertEquals(503,assertThrows(AuthFailure.class,()->service(true).register(input,"peer")).status());verify(repository,never()).insert(any(Account.class));
    }
    @Test void passwordValidationRejectsBcryptTruncationAndWhitespace() {
        assertThrows(AuthFailure.class,()->AuthInput.newPassword("ü".repeat(40)));
        assertThrows(AuthFailure.class,()->AuthInput.newPassword(" ".repeat(20)));
        assertThrows(AuthFailure.class,()->AuthInput.newPassword("short"));
        AuthInput.newPassword("ü".repeat(20));
    }
}
