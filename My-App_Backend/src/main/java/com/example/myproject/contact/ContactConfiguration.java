package com.example.myproject.contact;

import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableConfigurationProperties(ContactProperties.class)
@PropertySource("classpath:contact.properties")
public class ContactConfiguration implements WebMvcConfigurer {
    public static final List<String> ORIGINS = List.of("https://basel-homefixer.ch",
        "http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:5180", "http://127.0.0.1:5180");
    public static CorsConfiguration cors() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(ORIGINS);
        cors.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Content-Type"));
        cors.setAllowCredentials(false);
        return cors;
    }
    @Override public void addCorsMappings(CorsRegistry registry) {
        for (String path : List.of("/api/contact", "/api/contact/status")) {
            registry.addMapping(path).allowedOrigins(ORIGINS.toArray(String[]::new))
                .allowedMethods("GET", "POST", "OPTIONS").allowedHeaders("Content-Type").allowCredentials(false);
        }
    }
    @Bean("contactMailSender")
    public JavaMailSenderImpl contactMailSender(ContactProperties config) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.getSmtpHost()); sender.setPort(config.getSmtpPort());
        sender.setUsername(ContactProperties.MAILBOX); sender.setPassword(config.getSmtpPassword());
        sender.setDefaultEncoding("UTF-8");
        var props = sender.getJavaMailProperties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.ssl.enable", Boolean.toString(config.isSmtpSsl()));
        props.setProperty("mail.smtp.ssl.checkserveridentity", "true");
        props.setProperty("mail.smtp.starttls.enable", Boolean.toString(!config.isSmtpSsl()));
        props.setProperty("mail.smtp.starttls.required", Boolean.toString(!config.isSmtpSsl()));
        props.setProperty("mail.smtp.connectiontimeout", "5000");
        props.setProperty("mail.smtp.timeout", "5000");
        props.setProperty("mail.smtp.writetimeout", "5000");
        return sender;
    }
}
