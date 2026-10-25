package com.example.myproject.content;

import com.example.myproject.config.SecurityConfig;
import java.net.URI;
import java.net.http.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = PublicContentServerTest.ServerConfiguration.class,
    properties = {"spring.config.location=optional:classpath:/content-server-test-only.properties", "logging.level.root=ERROR"})
class PublicContentServerTest {
    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class})
    @Import({PublicContentController.class, PublicMediaController.class, PublicContentService.class, ContentConfiguration.class, SecurityConfig.class})
    static class ServerConfiguration {}

    @LocalServerPort int port;
    @MockitoBean ContentStore store;

    @Test void realErrorDispatchPreserves404And400WithoutExposingDrafts() throws Exception {
        var draft = new ProjectContent();
        draft.id = "draft"; draft.status = "draft";
        when(store.byId(ProjectContent.class, "draft")).thenReturn(draft);
        var http = HttpClient.newHttpClient();
        for (String path : java.util.List.of("/api/projects/by-id/draft?lang=de", "/api/media/12345678-1234-1234-1234-123456789abc/small")) {
            var response = get(http, path);
            assertEquals(404, response.statusCode(), "Error dispatch must not replace 404 with 401");
            assertFalse(response.body().contains("coverImageId"));
        }
        assertEquals(400, get(http, "/api/projects?size=999").statusCode());
        assertEquals(401, get(http, "/api/admin/projects").statusCode());
    }
    HttpResponse<String> get(HttpClient client, String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }
}
