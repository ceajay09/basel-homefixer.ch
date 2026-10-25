package ch.baselhomefixer.preview;

import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import org.bson.Document;

/** Temporary visibility test restricted to the isolated preview database. */
public final class PreviewPublicationCheck {
    public static void main(String[] args) throws Exception {
        if (!"basel_homefixer_preview".equals(System.getenv("SPRING_DATA_MONGODB_DATABASE")))
            throw new IllegalArgumentException("Unexpected database");
        try (var client = MongoClients.create(System.getenv("SPRING_DATA_MONGODB_URI"))) {
            var db = client.getDatabase("basel_homefixer_preview");
            var projects = db.getCollection("projects");
            var original = projects.find(Filters.eq("_id", "entrance-door")).first();
            if (original == null || !"published".equals(original.getString("status"))) throw new IllegalStateException("Unexpected preview content");
            String image = original.getString("coverImageId");
            var http = HttpClient.newHttpClient();
            String base = "http://127.0.0.1:8081";
            if (status(http, base + "/api/media/" + image + "/small") != 200) throw new AssertionError("Published image missing");
            try {
                projects.updateOne(Filters.eq("_id", "entrance-door"), Updates.set("status", "draft"));
                if (status(http, base + "/api/projects/by-id/entrance-door?lang=de") != 404) throw new AssertionError("Draft project exposed");
                if (status(http, base + "/api/media/" + image + "/small") != 404) throw new AssertionError("Draft image exposed");
                System.out.println("PASS_REAL_MONGO_DRAFT_PROJECT_AND_IMAGE_HIDDEN");
            } finally {
                projects.updateOne(Filters.eq("_id", "entrance-door"), Updates.set("status", original.getString("status")));
            }
            if (status(http, base + "/api/media/" + image + "/small") != 200) throw new AssertionError("Restored image missing");
            for (String collection : java.util.List.of("projects", "blogpost")) {
                boolean unique = false;
                for (Document index : db.getCollection(collection).listIndexes()) {
                    if (index.get("key", Document.class).containsKey("slug") && Boolean.TRUE.equals(index.getBoolean("unique"))) unique = true;
                }
                if (!unique) throw new AssertionError("Missing unique slug index");
            }
            if (projects.countDocuments() != 6 || db.getCollection("media").countDocuments() != 6 || db.getCollection("blogpost").countDocuments() != 0)
                throw new AssertionError("Unexpected preview record counts");
            System.out.println("PASS_REAL_MONGO_UNIQUE_SLUG_INDEXES_AND_RESTORED_SIX_PROJECTS");
        }
    }
    static int status(HttpClient client, String uri) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(uri)).timeout(Duration.ofSeconds(10)).GET().build(), HttpResponse.BodyHandlers.discarding()).statusCode();
    }
}
