package com.example.myproject.content;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class ContentConfiguration implements WebMvcConfigurer {
    public static final List<String> PATHS=List.of("/api/projects/**","/api/blog/**","/api/media/**","/api/content/**");
    public static final List<String> ORIGINS=List.of("https://basel-homefixer.ch","http://localhost:5173","http://127.0.0.1:5173","http://localhost:5180","http://127.0.0.1:5180");
    public static CorsConfiguration cors(){var c=new CorsConfiguration();c.setAllowedOrigins(ORIGINS);c.setAllowedMethods(List.of("GET","HEAD","OPTIONS"));c.setAllowedHeaders(List.of("Accept","Content-Type"));return c;}
    @Override public void addCorsMappings(CorsRegistry registry){for(String path:PATHS)registry.addMapping(path).allowedOrigins(ORIGINS.toArray(String[]::new)).allowedMethods("GET","HEAD","OPTIONS");}
    @Bean public MediaStorage mediaStorage(@Value("${CONTENT_MEDIA_ROOT:/data/content/media}") String root){return new MediaStorage(Path.of(root));}
}
