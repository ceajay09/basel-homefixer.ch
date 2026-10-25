package com.example.myproject.admin;

import com.example.myproject.content.*;
import com.example.myproject.model.Blogpost;
import java.util.*;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

/** Conditional writes are atomic MongoDB updates, not a read-then-save sequence. */
@Repository
public class AdminContentStore {
    private final MongoTemplate mongo;
    public AdminContentStore(MongoTemplate mongo) { this.mongo=mongo; }
    public static long version(Long version) { return version==null?0:version; }
    public static Criteria expected(long version) {
        return version==0?new Criteria().orOperator(Criteria.where("version").is(0),Criteria.where("version").is(null)):Criteria.where("version").is(version);
    }
    public <T extends ContentFields> T get(Class<T> type,String id) { return mongo.findById(id,type); }
    public MediaAsset media(String id) { return mongo.findById(id,MediaAsset.class); }
    private Query listQuery(String status) {
        return status==null?new Query():Query.query("legacy".equals(status)?Criteria.where("status").is(null):Criteria.where("status").is(status));
    }
    public <T extends ContentFields> List<T> list(Class<T> type,String status,int page,int size) {
        return mongo.find(listQuery(status).with(Sort.by(Sort.Order.desc("updatedAt"),Sort.Order.asc("id"))).skip((long)page*size).limit(size),type);
    }
    public long count(Class<? extends ContentFields> type,String status) { return mongo.count(listQuery(status),type); }
    public boolean slugTaken(Class<? extends ContentFields> type,String slug,String exceptId) {
        return mongo.exists(Query.query(Criteria.where("slug").is(slug).and("id").ne(exceptId)),type);
    }
    public void ensureSlugIndex(Class<? extends ContentFields> type) {
        mongo.indexOps(type).ensureIndex(new Index().on("slug",Sort.Direction.ASC).unique()
            .partial(PartialIndexFilter.of(Criteria.where("slug").type(2))));
    }
    public <T extends ContentFields> T insert(T content) { return mongo.insert(content); }
    public boolean replace(ContentFields content,long expectedVersion) {
        Document fields=new Document();mongo.getConverter().write(content,fields);fields.remove("_id");
        Update update=new Update();
        // Null optional values must clear previously stored facts instead of silently retaining them.
        for(String field:List.of("slug","title","excerpt","description","categories","coverImageId","coverAlt","mediaIds","status","publishedAt","version","updatedAt"))
            if(fields.containsKey(field))update.set(field,fields.get(field));else update.unset(field);
        if(content instanceof ProjectContent) for(String field:List.of("location","challenge","result","tags","dates","duration","investmentChf","areaM2","apartments","thermostats","doors","complexity","homeOrder"))
            if(fields.containsKey(field))update.set(field,fields.get(field));else update.unset(field);
        return mongo.updateFirst(Query.query(Criteria.where("id").is(content.id)).addCriteria(expected(expectedVersion)),update,content.getClass()).getMatchedCount()==1;
    }
    public HomepageSelection selection() {
        var configured=mongo.findById(HomepageSelection.ID,HomepageSelection.class);
        if(configured!=null)return configured;
        var fallback=new HomepageSelection();fallback.version=0L;
        fallback.projectIds=mongo.find(Query.query(Criteria.where("homeOrder").ne(null).gte(0)).with(Sort.by("homeOrder","id")).limit(3),ProjectContent.class).stream().map(p->p.id).toList();
        return fallback;
    }
    public boolean replaceSelection(HomepageSelection selection,long expectedVersion) {
        var query=Query.query(Criteria.where("id").is(HomepageSelection.ID)).addCriteria(expected(expectedVersion));
        if(mongo.updateFirst(query,new Update().set("projectIds",selection.projectIds).set("version",selection.version),HomepageSelection.class).getMatchedCount()==1)return true;
        if(expectedVersion!=0)return false;
        try{mongo.insert(selection);return true;}catch(DuplicateKeyException conflict){return false;}
    }
    public List<MediaAsset> mediaList(int page,int size) { return mongo.find(new Query().with(Sort.by("id")).skip((long)page*size).limit(size),MediaAsset.class); }
    public long mediaCount() { return mongo.count(new Query(),MediaAsset.class); }
    public MediaAsset insertMedia(MediaAsset asset) { return mongo.insert(asset); }
    public boolean replaceAlt(String id,Map<String,String> alt,long version) {
        return mongo.updateFirst(Query.query(Criteria.where("id").is(id)).addCriteria(expected(version)),
            new Update().set("alt",alt).set("version",version+1),MediaAsset.class).getMatchedCount()==1;
    }
}
