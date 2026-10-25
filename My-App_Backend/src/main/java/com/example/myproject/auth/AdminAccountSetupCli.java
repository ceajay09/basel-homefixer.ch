package com.example.myproject.auth;

import com.example.myproject.model.Account;
import com.mongodb.client.MongoClients;
import java.io.Console;
import java.util.*;
import java.util.regex.Pattern;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Never a Spring runner or HTTP endpoint. No app-config or implicit database connection. */
public final class AdminAccountSetupCli {
    public static void main(String[] args) {
        try { execute(args); }
        catch (Exception failure) {
            // Never print connection strings, input, account documents or exception messages.
            System.err.println("Account setup failed (" + failure.getClass().getSimpleName() + "). Check explicit configuration and target state.");
            System.exit(1);
        }
    }
    static void execute(String[] args) throws Exception {
        Map<String,String> options = new HashMap<>();
        for (int i=0; i<args.length; i++) {
            if ("--apply".equals(args[i])) options.put("--apply", "true");
            else if (Set.of("--mode", "--email", "--database", "--confirm-database").contains(args[i]) && i+1<args.length) options.put(args[i], args[++i]);
            else throw new IllegalArgumentException("Invalid option");
        }
        String mode=options.getOrDefault("--mode","create-admin"), database=options.get("--database");
        if (!Set.of("create-admin","reset-password").contains(mode) || database==null || !database.matches("[A-Za-z0-9_-]{1,63}")) throw new IllegalArgumentException("Invalid target");
        String email=AuthInput.email(options.get("--email"));
        System.out.println("Mode: " + mode + "; database: " + database);
        if (!options.containsKey("--apply")) {
            System.out.println("DRY RUN: arguments validated. No database connection, password input, index changes or account writes. Existing-account conflicts not checked."); return;
        }
        if (!database.equals(options.get("--confirm-database"))) throw new IllegalArgumentException("Explicit database confirmation required");
        String uri=System.getenv("AUTH_SETUP_MONGO_URI");
        if (uri==null || uri.isBlank()) throw new IllegalArgumentException("AUTH_SETUP_MONGO_URI required");
        Console console=System.console();
        if (console==null) throw new IllegalStateException("Interactive console required for hidden password input");
        char[] first=console.readPassword("New password (12+ characters, max 72 UTF-8 bytes): ");
        char[] second=console.readPassword("Repeat new password: ");
        if (first==null || second==null) throw new IllegalArgumentException("Password entry cancelled");
        try {
            if (!Arrays.equals(first,second)) throw new IllegalArgumentException("Password entries differ");
            AuthInput.newPassword(new String(first));
            try (var client=MongoClients.create(uri)) {
                var mongo=new MongoTemplate(client,database);
                var matches=mongo.find(Query.query(Criteria.where("email").regex("^"+Pattern.quote(email)+"$","i")),Account.class);
                if (matches.size()>1) throw new IllegalStateException("Ambiguous legacy email; review required");
                Account existing=matches.isEmpty()?null:matches.get(0);
                if (("create-admin".equals(mode) && existing!=null) || ("reset-password".equals(mode) && existing==null)) throw new IllegalStateException("Target state does not match requested operation");
                String previousHash=existing==null?null:existing.getPassword();
                String previousRevision=existing==null?null:existing.getAuthRevision();
                Account prepared=AdminAccountSetup.prepare(mode,existing,email,new String(first),new BCryptPasswordEncoder(12));
                AuthAccountIndexes.ensure(mongo); // Fails on conflicting emails, never silently repairs old records.
                if (existing==null) mongo.insert(prepared);
                else {
                    // Only credentials change; never save a stale full profile over concurrent edits.
                    var selector=Query.query(Criteria.where("_id").is(existing.getID())
                        .and("password").is(previousHash).and("authRevision").is(previousRevision));
                    var result=mongo.updateFirst(selector,new Update().set("password",prepared.getPassword()).set("authRevision",prepared.getAuthRevision()),Account.class);
                    if (result.getModifiedCount()!=1) throw new IllegalStateException("Concurrent credential change; retry after review");
                }
                System.out.println("Explicit account operation completed; existing permission preserved for reset.");
            }
        } finally { Arrays.fill(first,(char)0); Arrays.fill(second,(char)0); }
    }
}

