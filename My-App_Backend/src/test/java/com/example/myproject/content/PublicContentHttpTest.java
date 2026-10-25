package com.example.myproject.content;
import com.example.myproject.config.SecurityConfig;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;import java.util.*;import java.time.Instant;
import static org.mockito.Mockito.*;import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest({PublicContentController.class,PublicMediaController.class})
@ContextConfiguration(classes={PublicContentController.class,PublicMediaController.class,PublicContentService.class,ContentConfiguration.class,SecurityConfig.class})
@TestPropertySource(properties={"spring.config.location=optional:classpath:/content-test-only.properties","logging.level.root=ERROR"})
class PublicContentHttpTest {
    @Autowired MockMvc mvc;@MockitoBean ContentStore store;@MockitoBean MediaStorage storage;@TempDir Path temp;
    @Test void publishedProjectsAreLocalizedAndPagingIsBounded() throws Exception {
        var p=new ProjectContent();p.id="kitchen";p.slug="kitchen";p.title=Map.of("de","Küche","en","Kitchen");p.status="published";p.publishedAt=Instant.now().minusSeconds(60);p.categories=List.of("kitchen");
        when(store.list(ProjectContent.class,null,0,12,false)).thenReturn(List.of(p));when(store.count(ProjectContent.class,null,false)).thenReturn(1L);
        mvc.perform(get("/api/projects?lang=de")).andExpect(status().isOk()).andExpect(jsonPath("$.items[0].title").value("Küche"));
        mvc.perform(get("/api/projects?size=500")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/content/categories?type=projects&lang=en")).andExpect(status().isOk()).andExpect(jsonPath("$[0].label").value("Kitchens"));
    }
    @Test void mediaAccessRequiresPublishedReferenceOnEveryRequest() throws Exception {
        String id="12345678-1234-1234-1234-123456789abc";var asset=new MediaAsset();asset.id=id;asset.variants.put("small",new MediaAsset.Variant(1,1,"hash"));Path file=temp.resolve("small.jpg");Files.write(file,new byte[]{1,2,3});
        when(store.media(id)).thenReturn(asset);when(storage.publicFile(id,"small")).thenReturn(file);
        mvc.perform(get("/api/media/"+id+"/small")).andExpect(status().isNotFound());verifyNoInteractions(storage);
        when(store.publicReference(id)).thenReturn(true);
        mvc.perform(get("/api/media/"+id+"/small")).andExpect(status().isOk()).andExpect(content().contentType("image/jpeg")).andExpect(header().string("Cache-Control","no-store"));
        when(store.publicReference(id)).thenReturn(false);
        mvc.perform(get("/api/media/"+id+"/small?preview=true")).andExpect(status().isNotFound());
        mvc.perform(get("/api/media/"+id+"/original")).andExpect(status().isNotFound());
    }
    @Test void noPublicWritesUploadsOrAdminRoutes() throws Exception {
        for(String path:List.of("/api/projects","/api/blog","/api/media/upload","/api/content/import","/api/admin/projects"))mvc.perform(post(path).contentType("application/json").content("{}")).andExpect(status().is4xxClientError());
        verifyNoInteractions(store);
    }
    @Test void localReadCorsWorksButForeignOriginDoesNot() throws Exception {
        mvc.perform(options("/api/projects").header("Origin","http://127.0.0.1:5180").header("Access-Control-Request-Method","GET")).andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://127.0.0.1:5180"));
        mvc.perform(options("/api/media/123/small").header("Origin","https://untrusted.example").header("Access-Control-Request-Method","GET")).andExpect(status().isForbidden());
    }
}
