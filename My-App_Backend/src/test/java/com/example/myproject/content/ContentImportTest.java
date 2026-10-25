package com.example.myproject.content;
import org.junit.jupiter.api.*;import org.junit.jupiter.api.io.TempDir;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.*;import java.util.*;import javax.imageio.ImageIO;import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;
class ContentImportTest {
    @TempDir Path temp;ObjectMapper json=new ObjectMapper().findAndRegisterModules();
    static class MemoryTarget implements ContentImport.Target {
        Map<String,ContentFields> contents=new HashMap<>();Map<String,MediaAsset> media=new HashMap<>();int writes;
        public ContentFields byId(Class<? extends ContentFields> type,String id){return contents.get(type.getName()+id);}
        public ContentFields bySlug(Class<? extends ContentFields> type,String slug){return contents.values().stream().filter(c->c.getClass()==type&&slug.equals(c.slug)).findFirst().orElse(null);}
        public MediaAsset media(String id){return media.get(id);}public void ensureIndexes(){}
        public void save(ContentFields c){contents.put(c.getClass().getName()+c.id,c);writes++;}public void saveMedia(MediaAsset m){media.put(m.id,m);writes++;}
    }
    Path bundle(String title) throws Exception {
        Path source=temp.resolve("input");Files.createDirectories(source);ImageIO.write(new BufferedImage(30,20,BufferedImage.TYPE_INT_RGB),"png",source.resolve("photo.png").toFile());
        var b=new ContentImport.Bundle();var p=new ProjectContent();p.id="kitchen";p.slug="kitchen";p.status="draft";p.title=Map.of("de",title,"en","Kitchen");p.description=Map.of("de","Beschreibung","en","Description");p.excerpt=p.description;p.location=Map.of("de","Basel","en","Basel");p.tags=Map.of("de",List.of("Küche"),"en",List.of("Kitchen"));p.dates=List.of("2026-06");p.categories=List.of("kitchen");p.coverImageId="image";
        b.projects.add(p);b.media.put("image",new ContentImport.Source("photo.png",Map.of("de","Küche","en","Kitchen")));Path file=temp.resolve("manifest.json");json.writeValue(file.toFile(),b);return file;
    }
    @Test void dryRunHasNoWritesAndRepeatedApplyIsIdempotent() throws Exception {
        Path file=bundle("Küche"),root=temp.resolve("media");var importer=new ContentImport(json,new MediaStorage(root));var target=new MemoryTarget();
        var plan=importer.prepare(file,temp.resolve("input"),false);assertFalse(Files.exists(root));assertTrue(target.contents.isEmpty());
        importer.apply(plan,target,false);assertEquals(2,target.writes);assertEquals("draft",target.contents.values().iterator().next().status);
        importer.apply(importer.prepare(file,temp.resolve("input"),false),target,false);assertEquals(2,target.writes);
        assertEquals("kitchen",target.contents.values().iterator().next().id);
    }
    @Test void updatesRequireExplicitDraftPermissionAndProtectPublication() throws Exception {
        var importer=new ContentImport(json,new MediaStorage(temp.resolve("media")));var target=new MemoryTarget();importer.apply(importer.prepare(bundle("Alt"),temp.resolve("input"),false),target,false);
        var changed=importer.prepare(bundle("Neu"),temp.resolve("input"),false);assertThrows(IllegalArgumentException.class,()->importer.apply(changed,target,false));
        importer.apply(changed,target,true);assertEquals("Neu",target.contents.values().iterator().next().title.get("de"));
        target.contents.values().iterator().next().status="published";
        assertThrows(IllegalArgumentException.class,()->importer.apply(importer.prepare(bundle("Dritter"),temp.resolve("input"),false),target,true));
    }
    @Test void rejectsDuplicateSlugsMissingMediaAndUnintendedPublish() throws Exception {
        Path file=bundle("Küche");var b=json.readValue(file.toFile(),ContentImport.Bundle.class);var second=json.convertValue(b.projects.get(0),ProjectContent.class);second.id="other";b.projects.add(second);json.writeValue(file.toFile(),b);
        var importer=new ContentImport(json,new MediaStorage(temp.resolve("media")));assertThrows(IllegalArgumentException.class,()->importer.prepare(file,temp.resolve("input"),false));
        b.projects.remove(1);b.projects.get(0).coverImageId="missing";json.writeValue(file.toFile(),b);assertThrows(IllegalArgumentException.class,()->importer.prepare(file,temp.resolve("input"),false));
        b.projects.get(0).coverImageId="image";b.projects.get(0).status="published";b.projects.get(0).publishedAt=java.time.Instant.now();json.writeValue(file.toFile(),b);assertThrows(IllegalArgumentException.class,()->importer.prepare(file,temp.resolve("input"),false));
    }
    @Test void existingSlugCannotBeReassignedToAnotherId() throws Exception {
        var importer=new ContentImport(json,new MediaStorage(temp.resolve("media")));var target=new MemoryTarget();Path file=bundle("Küche");var plan=importer.prepare(file,temp.resolve("input"),false);
        var other=new ProjectContent();other.id="different";other.slug="kitchen";target.save(other);assertThrows(IllegalArgumentException.class,()->importer.apply(plan,target,true));assertFalse(Files.exists(temp.resolve("media")));
    }
    @Test void importerCannotOverwriteContentAlreadyManagedThroughVersionedApi() throws Exception {
        var importer=new ContentImport(json,new MediaStorage(temp.resolve("media")));var target=new MemoryTarget();
        var plan=importer.prepare(bundle("Original"),temp.resolve("input"),false);importer.apply(plan,target,false);
        target.contents.values().iterator().next().version=1L;
        var update=importer.prepare(bundle("Imported change"),temp.resolve("input"),false);
        assertThrows(IllegalArgumentException.class,()->importer.apply(update,target,true));
        assertEquals("Original",target.contents.values().iterator().next().title.get("de"));
    }
}
