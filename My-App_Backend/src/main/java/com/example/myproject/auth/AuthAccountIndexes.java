package com.example.myproject.auth;

import com.example.myproject.model.Account;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/** Read-only readiness check at registration; index creation is an explicit local setup action. */
@Component
public class AuthAccountIndexes {
    private final MongoTemplate mongo;
    public AuthAccountIndexes(MongoTemplate mongo) { this.mongo = mongo; }
    public boolean registrationReady() {
        for (Document index : mongo.getCollection(mongo.getCollectionName(Account.class)).listIndexes()) {
            var key = index.get("key", Document.class);
            var collation = index.get("collation", Document.class);
            var partial = index.get("partialFilterExpression", Document.class);
            boolean allEmailStrings = partial == null || partial.equals(new Document("email", new Document("$type", 2)))
                || partial.equals(new Document("email", new Document("$type", "string")));
            if (allEmailStrings && key != null && key.size() == 1 && Integer.valueOf(1).equals(key.get("email"))
                && Boolean.TRUE.equals(index.getBoolean("unique")) && collation != null
                && "en".equals(collation.getString("locale")) && Integer.valueOf(2).equals(collation.get("strength"))
                && !Boolean.TRUE.equals(collation.getBoolean("caseLevel"))) return true;
        }
        return false;
    }
    public static void ensure(MongoTemplate mongo) {
        mongo.indexOps(Account.class).ensureIndex(new Index().on("email", Sort.Direction.ASC).unique().named("auth_email_unique")
            .collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()))
            .partial(PartialIndexFilter.of(Criteria.where("email").type(2))));
    }
}
