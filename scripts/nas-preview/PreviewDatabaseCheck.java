package ch.baselhomefixer.preview;

import com.mongodb.client.MongoClients;
import java.nio.file.*;
import java.util.*;
import org.bson.Document;

/** Checks and snapshots only the explicitly isolated preview database. */
public final class PreviewDatabaseCheck {
    public static void main(String[] args) throws Exception {
        String name = System.getenv("SPRING_DATA_MONGODB_DATABASE");
        if (!"basel_homefixer_preview".equals(name)) throw new IllegalArgumentException("Unexpected preview database");
        try (var client = MongoClients.create(System.getenv("SPRING_DATA_MONGODB_URI"))) {
            var database = client.getDatabase(name);
            database.runCommand(new Document("ping", 1));
            Document snapshot = new Document("database", name).append("capturedAt", new Date());
            for (String collection : List.of("projects", "blogpost", "media")) {
                var records = database.getCollection(collection).find().into(new ArrayList<Document>());
                snapshot.append(collection, records);
                System.out.println(collection + "=" + records.size());
            }
            Files.writeString(Path.of("/ops/db-before-import.json"), snapshot.toJson(), StandardOpenOption.CREATE_NEW);
            System.out.println("PREVIEW_DATABASE_CHECK_OK");
        }
    }
}
