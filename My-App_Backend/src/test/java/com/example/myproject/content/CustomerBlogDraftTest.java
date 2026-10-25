package com.example.myproject.content;

import com.example.myproject.model.Blogpost;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerBlogDraftTest {
    @TempDir Path temp;
    @Test void actualCustomerDraftsImportIdempotentlyAndRemainPrivate() throws Exception {
        var json=new ObjectMapper().findAndRegisterModules();var storage=new MediaStorage(temp.resolve("media"));
        var importer=new ContentImport(json,storage);var manifest=Path.of("content-import/blogs.json");
        var sources=Path.of("../design/Kundenpaket_20261024/Kundenpaket/assets");
        var plan=importer.prepare(manifest,sources,false);assertEquals(3,plan.contents().size());assertEquals(3,plan.media().size());
        assertFalse(Files.exists(temp.resolve("media")));
        var target=new ContentImportTest.MemoryTarget();importer.apply(plan,target,false);int writes=target.writes;
        importer.apply(importer.prepare(manifest,sources,false),target,false);assertEquals(writes,target.writes);
        assertEquals(3,target.contents.size());
        for(var record:target.contents.values()){
            assertInstanceOf(Blogpost.class,record);assertEquals("draft",record.status);assertNull(record.publishedAt);
            assertFalse(record.published(java.time.Instant.now()));
            assertThrows(org.springframework.web.server.ResponseStatusException.class,()->PublicContentService.requirePublic(record));
            assertTrue(record.description.get("en").contains("Translation draft for customer review"));
            assertTrue(Files.isReadable(storage.publicFile(record.coverImageId,"large")));
        }
        assertTrue(target.contents.values().stream().anyMatch(c->c.description.get("de").contains("4–6 Monate")));
        assertTrue(target.contents.values().stream().anyMatch(c->c.description.get("de").contains("4–8 Monate")));
    }
}
