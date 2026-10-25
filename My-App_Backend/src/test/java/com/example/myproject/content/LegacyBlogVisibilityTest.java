package com.example.myproject.content;
import org.junit.jupiter.api.Test;
import java.util.*;
import com.example.myproject.model.Blogpost;
import com.example.myproject.controller.RequestHandler;
import com.example.myproject.repository.*;
import com.example.myproject.service.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
class LegacyBlogVisibilityTest {
    @Test void oldGetRoutesCannotExposeUnpublishedDocuments() throws Exception {
        var store=mock(ContentStore.class);var service=new PublicContentService(store);var draft=new Blogpost();draft.id="old-id";draft.description=Map.of("de","Private draft");
        when(store.byId(Blogpost.class,"old-id")).thenReturn(draft);when(store.list(Blogpost.class,null,0,24,false)).thenReturn(List.of(draft));
        var controller=new RequestHandler(mock(PDFService.class),new BlogService(service),mock(HomepageConfigService.class));
        var mvc=MockMvcBuilders.standaloneSetup(controller).build();
        mvc.perform(get("/api/blogpost/old-id")).andExpect(status().isNotFound());mvc.perform(get("/api/getBlogposts")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }
}

