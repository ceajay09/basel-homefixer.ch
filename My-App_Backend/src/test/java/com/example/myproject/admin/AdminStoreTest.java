package com.example.myproject.admin;

import com.example.myproject.content.*;
import com.example.myproject.model.Blogpost;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.UpdateResult;
import java.util.*;
import java.time.Instant;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.query.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class AdminStoreTest {
    MappingMongoConverter converter() throws Exception {
        var conversions=MongoCustomConversions.create(adapter->{});
        var mapping=new MongoMappingContext();mapping.setSimpleTypeHolder(conversions.getSimpleTypeHolder());mapping.afterPropertiesSet();
        var converter=new MappingMongoConverter(NoOpDbRefResolver.INSTANCE,mapping);converter.setCustomConversions(conversions);converter.afterPropertiesSet();return converter;
    }
    @Test void conditionalMongoWriteIncludesVersionAndClearsRemovedOptionalFacts() throws Exception {
        var mongo=mock(MongoTemplate.class);var converter=converter();when(mongo.getConverter()).thenReturn(converter);
        when(mongo.updateFirst(any(Query.class),any(Update.class),eq(ProjectContent.class))).thenReturn(UpdateResult.acknowledged(1,1L,null));
        var project=new ProjectContent();project.id="stable-project";project.slug="stable";project.status="draft";project.version=1L;
        var store=new AdminContentStore(mongo);assertTrue(store.replace(project,0));
        var query=ArgumentCaptor.forClass(Query.class);var update=ArgumentCaptor.forClass(Update.class);verify(mongo).updateFirst(query.capture(),update.capture(),eq(ProjectContent.class));
        var mapped=new QueryMapper(converter).getMappedObject(query.getValue().getQueryObject(),converter.getMappingContext().getPersistentEntity(ProjectContent.class));
        assertEquals("stable-project",mapped.get("_id"));assertNotNull(mapped.get("$or"));
        assertEquals(1L,update.getValue().getUpdateObject().get("$set",Document.class).get("version"));
        assertTrue(update.getValue().getUpdateObject().get("$unset",Document.class).containsKey("investmentChf"));
        assertFalse(update.getValue().getUpdateObject().get("$set",Document.class).containsKey("_id"));
        assertEquals(7L,AdminContentStore.expected(7).getCriteriaObject().get("version"));
    }
    @Test void uniquenessIsEnforcedByMongoIndexNotOnlyByPrecheck() {
        var mongo=mock(MongoTemplate.class);var operations=mock(IndexOperations.class);when(mongo.indexOps(Blogpost.class)).thenReturn(operations);
        new AdminContentStore(mongo).ensureSlugIndex(Blogpost.class);
        var index=ArgumentCaptor.forClass(IndexDefinition.class);verify(operations).ensureIndex(index.capture());
        assertEquals(1,index.getValue().getIndexKeys().get("slug"));assertEquals(true,index.getValue().getIndexOptions().get("unique"));
        assertNotNull(index.getValue().getIndexOptions().get("partialFilterExpression"));
    }
    @Test void publicHomepageUsesConfiguredOrderAndStillAppliesPublicationFilter() {
        var mongo=mock(MongoTemplate.class);var selection=new HomepageSelection();selection.projectIds=List.of("second","first");selection.version=2L;
        when(mongo.findById(HomepageSelection.ID,HomepageSelection.class)).thenReturn(selection);
        var first=new ProjectContent();first.id="first";var second=new ProjectContent();second.id="second";
        when(mongo.find(any(Query.class),eq(ProjectContent.class))).thenReturn(List.of(first,second));
        var result=new ContentStore(mongo).list(ProjectContent.class,null,0,3,true);assertEquals("second",result.get(0).id);
        var query=ArgumentCaptor.forClass(Query.class);verify(mongo).find(query.capture(),eq(ProjectContent.class));
        assertEquals("published",query.getValue().getQueryObject().get("status"));
        assertEquals(List.of("second","first"),query.getValue().getQueryObject().get("id",Document.class).get("$in"));
    }
    @Test void simultaneousEditorsCannotBothCommitTheirOriginalVersion() {
        var json=new ObjectMapper().findAndRegisterModules();var store=new MemoryAdminStore(json);var c=new ProjectContent();c.id="stable";c.slug="stable";c.version=1L;store.insert(c);
        var left=store.get(ProjectContent.class,"stable");var right=store.get(ProjectContent.class,"stable");left.version=2L;right.version=2L;
        left.title=Map.of("de","Newer");right.title=Map.of("de","Stale");
        assertTrue(store.replace(left,1));assertFalse(store.replace(right,1));assertEquals("Newer",store.get(ProjectContent.class,"stable").title.get("de"));
    }
}
