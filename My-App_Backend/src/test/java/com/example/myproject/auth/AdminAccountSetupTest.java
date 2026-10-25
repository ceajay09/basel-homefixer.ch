package com.example.myproject.auth;

import com.example.myproject.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
class AdminAccountSetupTest {
    final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(4);
    @Test void firstAdminRequiresNewAccountAndCreatesManagedHash() {
        Account account=AdminAccountSetup.prepare("create-admin",null,"Admin@Example.com","isolated-long-password",encoder);
        assertTrue(account.isAdmin());assertTrue(AuthInput.usable(account));assertEquals("admin@example.com",account.getEmail());
        assertTrue(encoder.matches("isolated-long-password",account.getPassword()));
        assertThrows(IllegalArgumentException.class,()->AdminAccountSetup.prepare("create-admin",account,"admin@example.com","another-long-password",encoder));
    }
    @Test void explicitLegacyResetPreservesIdProfileAndPermissionAndRotatesCredentials() {
        var old=new Account();old.setID("existing-id");old.setEmail("Person@Example.com");old.setPassword("plaintext-legacy");old.setFirstName("Existing");old.setAdmin(false);
        var result=AdminAccountSetup.prepare("reset-password",old,"person@example.com","new-isolated-password",encoder);
        assertEquals("existing-id",result.getID());assertEquals("Existing",result.getFirstName());assertEquals("Person@Example.com",result.getEmail());assertFalse(result.isAdmin());assertTrue(AuthInput.usable(result));
        String revision=result.getAuthRevision();AdminAccountSetup.prepare("reset-password",result,"person@example.com","another-new-password",encoder);assertNotEquals(revision,result.getAuthRevision());
    }
    @Test void dryRunNeedsNoPasswordOrDatabaseConnection() throws Exception {
        AdminAccountSetupCli.execute(new String[]{"--email","test@example.com","--database","isolated_auth_test"});
    }
    @Test void accountJsonNeverIncludesHashOrCredentialRevision() throws Exception {
        var account=AdminAccountSetup.prepare("create-admin",null,"test@example.com","isolated-long-password",encoder);
        String json=new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(account);
        assertFalse(json.contains("password"));assertFalse(json.contains("authRevision"));assertFalse(json.contains(account.getPassword()));
    }
}
