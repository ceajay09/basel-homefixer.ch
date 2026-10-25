package com.example.myproject.contact;

import com.example.myproject.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
@ContextConfiguration(classes={ContactController.class, ContactService.class, ContactErrors.class,
    ContactRateLimiter.class, ContactConfiguration.class, ContactBodyLimitFilter.class, SecurityConfig.class, com.example.myproject.config.WebConfig.class})
@TestPropertySource(properties={"spring.config.location=optional:classpath:/contact-test-only.properties", "logging.level.root=ERROR", "app.contact.enabled=true", "app.contact.smtp-password=local-test-only"})
class ContactControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired ContactProperties config;
    @MockitoBean(name="contactMailSender") JavaMailSender sender;
    @MockitoBean ContactRateLimiter limiter;
    Map<String,Object> valid() {
        Map<String,Object> data = new HashMap<>();
        data.put("name", "Test Person"); data.put("email", "visitor@example.com");
        data.put("phone", "+41 76 123 45 67"); data.put("service", "electrical");
        data.put("message", "A sufficiently long test message."); data.put("consent", true); data.put("website", "");
        return data;
    }
    @BeforeEach void configure() { config.setEnabled(true); config.setSmtpPassword("local-test-only"); config.setTrustedProxies(List.of()); when(limiter.acquire(anyString())).thenReturn(true); }
    @Test void sendsOnlyToFixedMailboxWithVisitorReplyTo() throws Exception {
        var data = valid(); data.put("to", "attacker@example.com"); data.put("from", "attacker@example.com");
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.code").value("accepted"));
        var capture = ArgumentCaptor.forClass(SimpleMailMessage.class); verify(sender).send(capture.capture());
        var mail = capture.getValue(); assertEquals(ContactProperties.MAILBOX, mail.getFrom());
        assertArrayEquals(new String[]{ContactProperties.MAILBOX}, mail.getTo()); assertNull(mail.getCc()); assertNull(mail.getBcc());
        assertEquals("visitor@example.com", mail.getReplyTo()); assertTrue(mail.getText().contains("Test Person"));
        assertTrue(mail.getText().contains("Elektroplanung")); assertTrue(mail.getText().contains("test message"));
    }
    @Test void rejectsInvalidFieldsAndHoneypotWithoutSending() throws Exception {
        for (String field : List.of("name", "email", "phone", "service", "message", "consent", "website")) {
            var data = valid();
            data.put(field, switch(field) { case "name" -> "x"; case "email" -> "bad\r\nBcc: x@example.com"; case "phone" -> "invalid"; case "service" -> "unknown"; case "message" -> "short"; case "consent" -> false; default -> "bot"; });
            mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("invalid_request"));
        }
        verifyNoInteractions(sender);
    }
    @Test void rejectsMissingAndOversizedInputs() throws Exception {
        for (var entry : Map.of("name",101,"email",255,"phone",41,"message",5001).entrySet()) {
            var data = valid(); data.put(entry.getKey(), "x".repeat(entry.getValue()));
            mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data))).andExpect(status().isBadRequest());
        }
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.consent").value("invalid"));
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content("broken"))
            .andExpect(status().isBadRequest()); verifyNoInteractions(sender);
    }
    @Test void acceptsEveryServiceAndNeutralSelection() throws Exception {
        List<String> values = new ArrayList<>(ContactService.SERVICES.keySet()); values.add("");
        for (String service : values) { var data=valid(); data.put("service",service);
            mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data))).andExpect(status().isOk()); }
        verify(sender,times(9)).send(any(SimpleMailMessage.class));
    }
    @Test void unavailableWithoutConfigAndNeverCallsSmtp() throws Exception {
        config.setSmtpPassword("");
        mvc.perform(get("/api/contact/status")).andExpect(status().isOk()).andExpect(jsonPath("$.available").value(false));
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(valid())))
            .andExpect(status().isServiceUnavailable()).andExpect(jsonPath("$.code").value("unavailable"));
        verifyNoInteractions(sender);
    }
    @Test void rejectsOversizedJsonBeforeDeserialisation() throws Exception {
        var data=valid();data.put("extra","x".repeat(33000));
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data)))
            .andExpect(status().isPayloadTooLarge()).andExpect(jsonPath("$.code").value("invalid_request"));
        verifyNoInteractions(sender);
    }
    @Test void enabledFlagAlsoRequired() throws Exception {
        config.setEnabled(false); mvc.perform(get("/api/contact/status")).andExpect(jsonPath("$.available").value(false));
        verifyNoInteractions(sender);
    }
    @Test void reportsSmtpFailureWithoutSuccess() throws Exception {
        doThrow(new MailSendException("test transport failure")).when(sender).send(any(SimpleMailMessage.class));
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(valid())))
            .andExpect(status().isBadGateway()).andExpect(jsonPath("$.code").value("send_failed"));
    }
    @Test void limitsAttemptsBeforeMailAndIgnoresSpoofedIp() throws Exception {
        when(limiter.acquire(anyString())).thenReturn(false);
        mvc.perform(post("/api/contact").header("X-Forwarded-For","203.0.113.3").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(valid())))
            .andExpect(status().isTooManyRequests()).andExpect(header().exists("Retry-After"));
        verify(limiter).acquire("127.0.0.1"); verifyNoInteractions(sender);
    }
    @Test void walksTrustedProxyChainFromRight() throws Exception {
        config.setTrustedProxies(List.of("127.0.0.1"));
        mvc.perform(post("/api/contact").header("X-Forwarded-For","198.51.100.99, 203.0.113.3").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(valid())))
            .andExpect(status().isOk()); verify(limiter).acquire("203.0.113.3");
    }
    @Test void onlyConfiguredOriginsPassContactPreflight() throws Exception {
        mvc.perform(options("/api/contact").header("Origin","http://127.0.0.1:5180").header("Access-Control-Request-Method","POST").header("Access-Control-Request-Headers","content-type"))
            .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://127.0.0.1:5180"));
        mvc.perform(options("/api/contact").header("Origin","https://untrusted.example").header("Access-Control-Request-Method","POST"))
            .andExpect(status().isForbidden());
        mvc.perform(get("/api/contact/status").header("Origin","http://127.0.0.1:5180"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.available").value(true));
    }
}
