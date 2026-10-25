package com.example.myproject.content;
import com.example.myproject.model.Blogpost;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.domain.Sort;
@Repository
public class ContentStore {
    final MongoTemplate mongo;
    public ContentStore(MongoTemplate mongo){this.mongo=mongo;}
    public static Criteria publication(){return Criteria.where("status").is("published").and("publishedAt").ne(null).lte(Instant.now());}
    private Query query(String category,boolean featured,HomepageSelection selection) {
        var q=Query.query(publication());
        if(category!=null&&!category.isBlank())q.addCriteria(Criteria.where("categories").is(category));
        if(featured) {
            q.addCriteria(Criteria.where("challenge.de").regex("\\S").and("challenge.en").regex("\\S")
                .and("result.de").regex("\\S").and("result.en").regex("\\S"));
            if(selection!=null)q.addCriteria(Criteria.where("id").in(selection.projectIds));
            else q.addCriteria(Criteria.where("homeOrder").ne(null).gte(0));
        }
        return q;
    }
    public <T extends ContentFields> List<T> list(Class<T> type,String category,int page,int size,boolean featured) {
        var selection=featured?mongo.findById(HomepageSelection.ID,HomepageSelection.class):null;
        var q=query(category,featured,selection);
        q.with(featured?Sort.by("homeOrder","id"):Sort.by(Sort.Order.desc("publishedAt"),Sort.Order.asc("id")));
        var found=mongo.find(q.skip((long)page*size).limit(size),type);
        if(featured&&selection!=null)found=new ArrayList<>(found);
        if(featured&&selection!=null)found.sort(Comparator.comparingInt(p->selection.projectIds.indexOf(p.id)));
        return found;
    }
    public <T extends ContentFields> long count(Class<T> type,String category,boolean featured) {
        var selection=featured?mongo.findById(HomepageSelection.ID,HomepageSelection.class):null;
        return mongo.count(query(category,featured,selection),type);
    }
    public <T extends ContentFields> T byId(Class<T> type,String id){return mongo.findById(id,type);}
    public <T extends ContentFields> T bySlug(Class<T> type,String slug){return mongo.findOne(Query.query(publication()).addCriteria(Criteria.where("slug").is(slug)),type);}
    public MediaAsset media(String id){return mongo.findById(id,MediaAsset.class);}
    public boolean publicReference(String id){
        var refs=new Criteria().orOperator(Criteria.where("coverImageId").is(id),Criteria.where("mediaIds").is(id));
        var q=Query.query(new Criteria().andOperator(publication(),refs));
        return mongo.exists(q,ProjectContent.class)||mongo.exists(q,Blogpost.class);
    }
    public boolean anyReference(String id){var q=Query.query(new Criteria().orOperator(Criteria.where("coverImageId").is(id),Criteria.where("mediaIds").is(id)));return mongo.exists(q,ProjectContent.class)||mongo.exists(q,Blogpost.class);}
}
