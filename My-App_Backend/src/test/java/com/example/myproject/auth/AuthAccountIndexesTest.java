package com.example.myproject.auth;

import com.example.myproject.model.Account;
import com.mongodb.client.*;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthAccountIndexesTest {
    @SuppressWarnings("unchecked")
    boolean ready(Document index) {
        var mongo=mock(MongoTemplate.class);var collection=mock(MongoCollection.class);
        var indices=mock(ListIndexesIterable.class);var cursor=mock(MongoCursor.class);
        when(mongo.getCollectionName(Account.class)).thenReturn("account");when(mongo.getCollection("account")).thenReturn(collection);
        when(collection.listIndexes()).thenReturn(indices);when(indices.iterator()).thenReturn(cursor);when(cursor.hasNext()).thenReturn(true,false);when(cursor.next()).thenReturn(index);
        return new AuthAccountIndexes(mongo).registrationReady();
    }
    Document index(){return new Document("key",new Document("email",1)).append("unique",true).append("collation",new Document("locale","en").append("strength",2));}
    @Test void registrationRequiresCaseInsensitiveUniquenessForAllEmailStrings() {
        assertTrue(ready(index()));
        assertTrue(ready(index().append("partialFilterExpression",new Document("email",new Document("$type",2)))));
        assertFalse(ready(index().append("unique",false)));
        assertFalse(ready(index().append("collation",new Document("locale","en").append("strength",2).append("caseLevel",true))));
        assertFalse(ready(index().append("collation",new Document("locale","simple"))));
        assertFalse(ready(index().append("partialFilterExpression",new Document("admin",true))));
    }
}
