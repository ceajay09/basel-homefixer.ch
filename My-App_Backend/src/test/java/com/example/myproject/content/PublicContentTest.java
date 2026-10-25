package com.example.myproject.content;
import com.example.myproject.model.Blogpost;
import java.time.*;import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;import static org.mockito.ArgumentMatchers.*;
class PublicContentTest {
    ContentStore store=mock(ContentStore.class);PublicContentService service=new PublicContentService(store);
    ProjectContent project(String status){var p=new ProjectContent();p.id="kitchen";p.slug="kitchen";p.status=status;p.publishedAt=Instant.now().minusSeconds(60);p.title=Map.of("de","Küche","en","Kitchen");p.categories=List.of("kitchen");p.dates=List.of("2026-06");p.tags=Map.of("de",List.of(),"en",List.of());return p;}
    @Test void draftMissingAndFutureDatesCannotBeReadDirectly(){
        for(String status:Arrays.asList(null,"draft","published")){var p=project(status);if("published".equals(status))p.publishedAt=Instant.now().plusSeconds(300);when(store.byId(ProjectContent.class,"kitchen")).thenReturn(p);assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.project("kitchen","de"));}
        when(store.byId(ProjectContent.class,"missing")).thenReturn(null);assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.project("missing","de"));
    }
    @Test void publishedListsAndFeaturedUseBoundedQueriesAndSharedFacts(){
        var p=project("published");p.apartments=18;p.description=Map.of("de","{{apartments}} Wohnungen","en","{{apartments}} apartments");
        when(store.list(ProjectContent.class,"kitchen",1,12,false)).thenReturn(List.of(p,project("draft")));when(store.count(ProjectContent.class,"kitchen",false)).thenReturn(13L);
        var list=service.projects("kitchen",1,12,false,"de");assertEquals(1,list.items().size());assertEquals("18 Wohnungen",list.items().get(0).description());assertEquals(2,list.totalPages());
        when(store.list(ProjectContent.class,null,0,3,true)).thenReturn(List.of(p));
        service.projects(null,0,24,true,"en");verify(store).list(ProjectContent.class,null,0,3,true);
        assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.projects("unknown",0,12,false,"de"));assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.projects(null,0,25,false,"de"));
    }
    @Test void blogDetailAndLegacyRoutesRejectDrafts(){
        var draft=new Blogpost();draft.id="old";draft.description=Map.of("de","Alt");when(store.bySlug(Blogpost.class,"old")).thenReturn(draft);when(store.byId(Blogpost.class,"old")).thenReturn(draft);when(store.list(Blogpost.class,null,0,24,false)).thenReturn(List.of(draft));
        assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.blog("old","de"));assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.legacyBlog("old"));assertTrue(service.legacyBlogs().isEmpty());
    }
    @Test void queryEnforcesPublicationDateCategoryAndDatabaseSort(){
        var mongo=mock(org.springframework.data.mongodb.core.MongoTemplate.class);var repo=new ContentStore(mongo);repo.list(ProjectContent.class,"heating",2,12,false);
        var q=org.mockito.ArgumentCaptor.forClass(org.springframework.data.mongodb.core.query.Query.class);verify(mongo).find(q.capture(),eq(ProjectContent.class));
        var query=q.getValue();assertEquals(12,query.getLimit());assertEquals(24,query.getSkip());assertEquals("published",query.getQueryObject().get("status"));assertEquals("heating",query.getQueryObject().get("categories"));assertNotNull(query.getQueryObject().get("publishedAt"));assertEquals(-1,query.getSortObject().get("publishedAt"));        repo.publicReference("image-id");
        var mediaQuery=org.mockito.ArgumentCaptor.forClass(org.springframework.data.mongodb.core.query.Query.class);
        verify(mongo).exists(mediaQuery.capture(),eq(ProjectContent.class));
        var clauses=(java.util.List<?>)mediaQuery.getValue().getQueryObject().get("$and");
        var publication=(org.bson.Document)clauses.get(0);assertEquals("published",publication.get("status"));assertNotNull(publication.get("publishedAt"));
        var references=(org.bson.Document)clauses.get(1);assertTrue(references.toJson().contains("coverImageId"));assertTrue(references.toJson().contains("mediaIds"));
    }
}

