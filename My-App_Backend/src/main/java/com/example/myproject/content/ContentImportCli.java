package com.example.myproject.content;
import com.example.myproject.model.Blogpost;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClients;
import java.nio.file.*;
import java.util.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.domain.Sort;

public final class ContentImportCli {
    public static void main(String[] args) throws Exception {
        var options=new HashMap<String,String>();for(int i=0;i<args.length;i++){String key=args[i];if(Set.of("--apply","--update-drafts","--allow-publish").contains(key))options.put(key,"true");else if(Set.of("--manifest","--source-root","--media-root","--database","--confirm-database").contains(key)&&i+1<args.length)options.put(key,args[++i]);else throw new IllegalArgumentException("Unknown/missing option");}
        for(String key:List.of("--manifest","--source-root","--media-root"))ContentImport.require(options.containsKey(key),"Required option: "+key);
        var storage=new MediaStorage(Path.of(options.get("--media-root")));var importer=new ContentImport(new ObjectMapper().findAndRegisterModules(),storage);
        var plan=importer.prepare(Path.of(options.get("--manifest")),Path.of(options.get("--source-root")),options.containsKey("--allow-publish"));
        System.out.println("Validated "+plan.contents().size()+" content records and "+plan.media().size()+" media records.");
        if(!options.containsKey("--apply")){System.out.println("DRY RUN: no database connection and no file writes. Database conflicts not checked.");return;}
        String database=options.get("--database"),uri=System.getenv("CONTENT_IMPORT_MONGO_URI");
        ContentImport.require(database!=null&&database.equals(options.get("--confirm-database")),"Database confirmation required");ContentImport.require(uri!=null&&!uri.isBlank(),"CONTENT_IMPORT_MONGO_URI required");
        try(var client=MongoClients.create(uri)){
            var mongo=new MongoTemplate(client,database);
            importer.apply(plan,new ContentImport.Target(){
                public ContentFields byId(Class<? extends ContentFields> type,String id){return mongo.findById(id,type);}
                public ContentFields bySlug(Class<? extends ContentFields> type,String slug){return mongo.findOne(Query.query(Criteria.where("slug").is(slug)),type);}
                public MediaAsset media(String id){return mongo.findById(id,MediaAsset.class);}
                public void ensureIndexes(){for(Class<?> type:List.of(ProjectContent.class,Blogpost.class))mongo.indexOps(type).ensureIndex(new Index().on("slug",Sort.Direction.ASC).unique().partial(PartialIndexFilter.of(Criteria.where("slug").type(2))));}
                public void save(ContentFields content){mongo.save(content);}public void saveMedia(MediaAsset asset){mongo.save(asset);}
            },options.containsKey("--update-drafts"));System.out.println("Explicit import completed.");
        }
    }
}
