package com.example.myproject.auth;

import com.example.myproject.config.SecurityConfig;
import com.example.myproject.content.*;
import com.example.myproject.contact.*;
import com.example.myproject.model.Account;
import com.example.myproject.repository.AccountRepository;
import com.example.myproject.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes=AuthenticationHttpTest.Configuration.class,
    properties={"spring.config.location=optional:classpath:/auth-server-test-only.properties","logging.level.root=ERROR",
        "AUTH_JWT_SECRET_BASE64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=","AUTH_PUBLIC_REGISTRATION_ENABLED=true"})
class AuthenticationHttpTest {
    @org.springframework.context.annotation.Configuration(proxyBeanMethods=false)
    @EnableAutoConfiguration(exclude={MongoAutoConfiguration.class,MongoDataAutoConfiguration.class,MongoRepositoriesAutoConfiguration.class})
    @Import({SecurityConfig.class,AuthController.class,AuthExceptionHandler.class,AccountService.class,Probe.class,
        PublicContentController.class,PublicMediaController.class,PublicContentService.class,ContentConfiguration.class,
        ContactController.class,ContactService.class,ContactConfiguration.class,ContactRateLimiter.class})
    static class Configuration {}
    @RestController static class Probe {
        @GetMapping("/api/admin/test-only-probe") Map<String,String> probe(){return Map.of("access","admin");}
    }
    @LocalServerPort int port;
    @Autowired ObjectMapper json;@Autowired TokenService tokens;@Autowired ContactProperties contact;
    @MockitoBean AccountRepository accounts;@MockitoBean AuthAccountIndexes indexes;@MockitoBean LoginRateLimiter limiter;
    @MockitoBean ContentStore contents;@MockitoBean(name="contactMailSender") JavaMailSender sender;
    final HttpClient http=HttpClient.newHttpClient();Account admin,user;
    @BeforeEach void fixtures() {
        admin=TokenServiceTest.account();admin.setID("admin-id");admin.setAdmin(true);admin.setEmail("admin@example.com");
        user=TokenServiceTest.account();user.setID("user-id");user.setEmail("user@example.com");
        when(accounts.findById("admin-id")).thenReturn(Optional.of(admin));when(accounts.findById("user-id")).thenReturn(Optional.of(user));
        when(accounts.findByEmailIgnoreCase("admin@example.com")).thenReturn(List.of(admin));when(accounts.findByEmailIgnoreCase("user@example.com")).thenReturn(List.of(user));
        when(indexes.registrationReady()).thenReturn(true);contact.setEnabled(false);
    }
    HttpResponse<String> request(String method,String path,String body,String token,String...extra) throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path));
        if(token!=null)builder.header("Authorization","Bearer "+token);
        if(body!=null)builder.header("Content-Type","application/json");
        for(int i=0;i<extra.length;i+=2)builder.header(extra[i],extra[i+1]);
        return http.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
    }
    @Test void correctLoginIssuesJsonAndValidatesCurrentAdminPermissionOnEachRequest() throws Exception {
        var login=request("POST","/api/login","{\"email\":\"ADMIN@example.com\",\"password\":\"isolated-password\"}",null);
        assertEquals(200,login.statusCode());assertEquals("no-store",login.headers().firstValue("Cache-Control").orElseThrow());
        String token=json.readTree(login.body()).get("token").asText();assertFalse(login.body().contains(admin.getPassword()));assertFalse(login.body().contains("authRevision"));
        assertEquals(200,request("GET","/api/admin/test-only-probe",null,token).statusCode());
        admin.setAdmin(false);assertEquals(403,request("GET","/api/admin/test-only-probe",null,token).statusCode());
    }
    @Test void missingInvalidExpiredAndBasicCredentialsCannotAccessProtectedNamespace() throws Exception {
        assertEquals(401,request("GET","/api/admin/test-only-probe",null,null).statusCode());
        assertEquals(401,request("GET","/api/admin/test-only-probe",null,"tampered-token").statusCode());
        var old=new TokenService(new AuthSettings(TokenServiceTest.SECRET,900,false),java.time.Clock.fixed(Instant.now().minusSeconds(1800),java.time.ZoneOffset.UTC));
        assertEquals(401,request("GET","/api/admin/test-only-probe",null,old.issue(admin).token()).statusCode());
        assertEquals(401,request("GET","/api/admin/test-only-probe",null,null,"Authorization","Basic dXNlcjpwYXNz").statusCode());
    }
    @Test void ordinaryUserCannotBecomeAdminUsingSignedRoleClaimsOrRegistrationInput() throws Exception {
        String userToken=tokens.issue(user).token();assertEquals(403,request("GET","/api/admin/test-only-probe",null,userToken).statusCode());
        var forgedRole=io.jsonwebtoken.Jwts.builder().issuer(TokenService.ISSUER).audience().add(TokenService.AUDIENCE).and().subject(user.getID()).claim("rev",user.getAuthRevision()).claim("admin",true)
            .issuedAt(Date.from(Instant.now().minusSeconds(1))).expiration(Date.from(Instant.now().plusSeconds(300))).signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(new byte[32])).compact();
        assertEquals(403,request("GET","/api/admin/test-only-probe",null,forgedRole).statusCode());
        var registered=request("POST","/api/register","{\"email\":\"new@example.com\",\"password\":\"new-isolated-password\",\"admin\":true,\"roles\":[\"ADMIN\"],\"authRevision\":\"attacker\"}",null);
        assertEquals(202,registered.statusCode());var capture=org.mockito.ArgumentCaptor.forClass(Account.class);verify(accounts).insert(capture.capture());
        assertFalse(capture.getValue().isAdmin());assertNotEquals("attacker",capture.getValue().getAuthRevision());assertTrue(new BCryptPasswordEncoder().matches("new-isolated-password",capture.getValue().getPassword()));
    }
    @Test void credentialsResetImmediatelyInvalidatesPreviouslyIssuedToken() throws Exception {
        String token=tokens.issue(admin).token();admin.setAuthRevision(UUID.randomUUID().toString());
        assertEquals(401,request("GET","/api/admin/test-only-probe",null,token).statusCode());
    }
    @Test void accountEndpointsExposeOnlyOwnMinimalDtoAndLogoutIsExplicitlyLocal() throws Exception {
        String token=tokens.issue(user).token();
        assertEquals(401,request("GET","/api/account/user-id",null,null).statusCode());
        assertEquals(403,request("GET","/api/account/admin-id",null,token).statusCode());
        var own=request("GET","/api/dashboard",null,token);assertEquals(200,own.statusCode());assertFalse(own.body().contains("password"));assertFalse(own.body().contains("authRevision"));
        assertEquals(200,request("POST","/api/logout",null,token).statusCode());
        assertEquals(200,request("GET","/api/dashboard",null,token).statusCode()); // Documented: token lives until expiration.
    }
    @Test void wrongUnknownAndLegacyLoginsAreGenericAndDoNotModifyAccounts() throws Exception {
        var wrong=request("POST","/api/login","{\"email\":\"admin@example.com\",\"password\":\"wrong-password\"}",null);
        var unknown=request("POST","/api/login","{\"email\":\"unknown@example.com\",\"password\":\"wrong-password\"}",null);
        assertEquals(401,wrong.statusCode());assertEquals(wrong.body(),unknown.body());
        admin.setPassword("legacy-password");admin.setAuthRevision(null);
        assertEquals(401,request("POST","/api/login","{\"email\":\"admin@example.com\",\"password\":\"legacy-password\"}",null).statusCode());
        verify(accounts,never()).save(any(Account.class));
    }
    @Test void rateLimitAndValidationDoNotLeakInputOrTrustForwardedHeaders() throws Exception {
        when(limiter.acquire(anyString(),eq("admin@example.com"))).thenReturn(123L);
        var result=request("POST","/api/login","{\"email\":\"admin@example.com\",\"password\":\"isolated-password\"}",null,"X-Forwarded-For","1.2.3.4");
        assertEquals(429,result.statusCode());assertEquals("123",result.headers().firstValue("Retry-After").orElseThrow());
        verify(limiter).acquire(argThat(peer->!peer.equals("1.2.3.4")),eq("admin@example.com"));
        assertEquals(400,request("POST","/api/login","{\"email\":\"\",\"password\":\"secret-input\"}",null).statusCode());
    }
    @Test void publicContentContactAndReadCorsRemainAvailable() throws Exception {
        when(contents.list(ProjectContent.class,null,0,12,false)).thenReturn(List.of());
        assertEquals(200,request("GET","/api/projects?lang=de",null,"invalid-but-irrelevant").statusCode());
        assertEquals(200,request("GET","/api/contact/status",null,null).statusCode());
        String valid="{\"name\":\"Test Person\",\"email\":\"visitor@example.com\",\"service\":\"kitchen\",\"message\":\"An isolated valid message\",\"consent\":true,\"website\":\"\"}";
        assertEquals(503,request("POST","/api/contact",valid,null).statusCode());verifyNoInteractions(sender);
        assertEquals(200,request("OPTIONS","/api/admin/test-only-probe",null,null,"Origin","http://127.0.0.1:5180","Access-Control-Request-Method","GET","Access-Control-Request-Headers","Authorization").statusCode());
        assertEquals(403,request("OPTIONS","/api/login",null,null,"Origin","https://untrusted.example","Access-Control-Request-Method","POST").statusCode());
    }
}
