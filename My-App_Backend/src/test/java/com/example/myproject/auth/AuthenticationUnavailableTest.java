package com.example.myproject.auth;

import com.example.myproject.repository.AccountRepository;
import com.example.myproject.content.ContentStore;
import com.example.myproject.content.ProjectContent;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes=AuthenticationHttpTest.Configuration.class,
    properties={"spring.config.location=optional:classpath:/auth-unavailable-test-only.properties","logging.level.root=ERROR","AUTH_JWT_SECRET_BASE64=","AUTH_PUBLIC_REGISTRATION_ENABLED=false"})
class AuthenticationUnavailableTest {
    @LocalServerPort int port;
    @MockitoBean AccountRepository accounts;@MockitoBean AuthAccountIndexes indexes;@MockitoBean LoginRateLimiter limiter;
    @MockitoBean ContentStore contents;@MockitoBean(name="contactMailSender") JavaMailSender sender;
    @Test void missingSecretFailsClosedWithoutBreakingPublicReadOrContact() throws Exception {
        var http=HttpClient.newHttpClient();String base="http://127.0.0.1:"+port;
        var login=HttpRequest.newBuilder(URI.create(base+"/api/login")).header("Content-Type","application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"test@example.com\",\"password\":\"isolated-password\"}")).build();
        var unavailable=http.send(login,HttpResponse.BodyHandlers.ofString());assertEquals(503,unavailable.statusCode());assertTrue(unavailable.body().contains("authentication_unavailable"));
        assertEquals(401,http.send(HttpRequest.newBuilder(URI.create(base+"/api/admin/test-only-probe")).GET().build(),HttpResponse.BodyHandlers.ofString()).statusCode());
        var registration=HttpRequest.newBuilder(URI.create(base+"/api/register")).header("Content-Type","application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"test@example.com\",\"password\":\"isolated-password\",\"admin\":true}")).build();
        assertEquals(403,http.send(registration,HttpResponse.BodyHandlers.ofString()).statusCode());
        when(contents.list(ProjectContent.class,null,0,12,false)).thenReturn(List.of());
        assertEquals(200,http.send(HttpRequest.newBuilder(URI.create(base+"/api/projects")).GET().build(),HttpResponse.BodyHandlers.ofString()).statusCode());
        assertEquals(200,http.send(HttpRequest.newBuilder(URI.create(base+"/api/contact/status")).GET().build(),HttpResponse.BodyHandlers.ofString()).statusCode());
        verifyNoInteractions(accounts);verifyNoInteractions(sender);
    }
}
