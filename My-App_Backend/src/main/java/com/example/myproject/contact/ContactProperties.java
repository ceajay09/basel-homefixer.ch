package com.example.myproject.contact;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.contact")
public class ContactProperties {
    public static final String MAILBOX = "baselhomefixer@gmail.com";
    private boolean enabled;
    private String smtpHost = "smtp.gmail.com";
    private int smtpPort = 587;
    private String smtpPassword = "";
    private boolean smtpSsl;
    private List<String> trustedProxies = List.of();
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }
    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String value) { smtpHost = value; }
    public int getSmtpPort() { return smtpPort; }
    public void setSmtpPort(int value) { smtpPort = value; }
    public String getSmtpPassword() { return smtpPassword; }
    public void setSmtpPassword(String value) { smtpPassword = value; }
    public boolean isSmtpSsl() { return smtpSsl; }
    public void setSmtpSsl(boolean value) { smtpSsl = value; }
    public List<String> getTrustedProxies() { return trustedProxies; }
    public void setTrustedProxies(List<String> value) { trustedProxies = value; }
    public boolean available() {
        return enabled && smtpPassword != null && !smtpPassword.isBlank()
            && smtpHost != null && !smtpHost.isBlank() && smtpPort > 0 && smtpPort <= 65535;
    }
}
