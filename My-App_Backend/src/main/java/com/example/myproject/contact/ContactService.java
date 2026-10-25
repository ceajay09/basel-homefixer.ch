package com.example.myproject.contact;

import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ContactService {
    public static final Map<String, String> SERVICES = Map.of(
        "kitchen", "Küche", "bathroom", "Badezimmer", "heating", "Heizung",
        "windows-doors", "Fenster & Türen", "electrical", "Elektroplanung und Installation",
        "smart-home", "Smart Home", "energy", "Energetische Renovation", "consulting", "Projektmanagement & Consulting");
    private final ContactProperties config;
    private final JavaMailSender sender;
    public ContactService(ContactProperties config, @Qualifier("contactMailSender") JavaMailSender sender) {
        this.config = config; this.sender = sender;
    }
    public boolean available() { return config.available(); }
    public void send(ContactRequest request) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(ContactProperties.MAILBOX); mail.setTo(ContactProperties.MAILBOX);
        mail.setReplyTo(request.email().trim());
        mail.setSubject("Website-Anfrage: " + SERVICES.getOrDefault(request.service() == null ? "" : request.service(), "Allgemeine Anfrage"));
        mail.setText("Name: " + request.name().trim() + "\nE-Mail: " + request.email().trim()
            + "\nTelefon: " + (request.phone() == null ? "" : request.phone().trim())
            + "\nDienstleistung: " + SERVICES.getOrDefault(request.service() == null ? "" : request.service(), "Allgemeine Anfrage")
            + "\nEinwilligung zur Kontaktaufnahme: Ja\n\n" + request.message().trim());
        sender.send(mail);
    }
}
