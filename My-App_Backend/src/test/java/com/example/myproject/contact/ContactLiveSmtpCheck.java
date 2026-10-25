package com.example.myproject.contact;

/** Explicit opt-in operator check; never a JUnit test or application startup action. */
public final class ContactLiveSmtpCheck {
    public static void main(String[] args) {
        if(args.length!=1 || !"--send-once".equals(args[0])) { System.out.println("NOT_SENT: explicit --send-once required"); System.exit(2); }
        var config=new ContactProperties();config.setEnabled(true);
        config.setSmtpPassword(System.getenv("CONTACT_SMTP_PASSWORD"));
        config.setSmtpHost(System.getenv().getOrDefault("CONTACT_SMTP_HOST","smtp.gmail.com"));
        config.setSmtpPort(Integer.parseInt(System.getenv().getOrDefault("CONTACT_SMTP_PORT","587")));
        config.setSmtpSsl(Boolean.parseBoolean(System.getenv().getOrDefault("CONTACT_SMTP_SSL","false")));
        if(!config.available() || !"smtp.gmail.com".equals(config.getSmtpHost()) || !(config.getSmtpPort()==587&&!config.isSmtpSsl() || config.getSmtpPort()==465&&config.isSmtpSsl())) {
            System.out.println("NOT_SENT: valid Gmail configuration required");System.exit(2);
        }
        try {
            var sender=new ContactConfiguration().contactMailSender(config);
            new ContactService(config,sender).send(new ContactRequest("Technischer Funktionstest",ContactProperties.MAILBOX,"","",
                "AUTORISIERTE TESTNACHRICHT – kein Kundenauftrag.\nEinmaliger Test des Kontaktversands von und an das konfigurierte Kundenpostfach.\nZeitpunkt UTC: "+java.time.Instant.now()+"\nBitte den Eingang im Postfach prüfen. Keine Antwort erforderlich.",true,""));
            System.out.println("SMTP_ACCEPTED: one test message accepted; inbox delivery not verified.");
        } catch(org.springframework.mail.MailAuthenticationException failure) { System.out.println("NOT_ACCEPTED: SMTP authentication failed. Check Gmail app password/account availability.");System.exit(3); }
        catch(org.springframework.mail.MailException failure) { System.out.println("SMTP_FAILED: transport failed; delivery status may be uncertain. No automatic retry.");System.exit(4); }
    }
}
