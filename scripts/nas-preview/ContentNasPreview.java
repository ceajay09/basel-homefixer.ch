package ch.baselhomefixer.preview;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.example.myproject.config.SecurityConfig;
import com.example.myproject.content.*;

/** Private NAS preview: intentionally imports only the public, read-only content API. */
public final class ContentNasPreview {
    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = MongoRepositoriesAutoConfiguration.class)
    @Import({ContentStore.class, PublicContentService.class, PublicContentController.class,
        PublicMediaController.class, ContentConfiguration.class, SecurityConfig.class})
    static class ReadOnlyConfiguration {}

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ReadOnlyConfiguration.class);
        application.setDefaultProperties(Map.of(
            "spring.config.location", "optional:file:/ops/preview.properties",
            "spring.main.banner-mode", "off",
            "server.port", "8081",
            "logging.level.root", "WARN",
            "logging.level.org.springframework.boot.autoconfigure.security", "ERROR",
            "management.endpoints.web.exposure.include", "health",
            "management.endpoint.health.show-details", "never"));
        application.run(args);
    }
}
