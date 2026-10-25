package com.example.myproject.admin;

import com.example.myproject.content.*;
import com.example.myproject.model.Blogpost;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class AdminContentService {
    public record Page<T>(List<T> items,int page,int size,long totalElements) {}
    public record Preview(JsonNode content,Map<String,JsonNode> media,String status,long version) {}
    private final AdminContentStore store;
    private final MediaStorage files;
    private final PublicContentService views;
    private final ObjectMapper json;
    public AdminContentService(AdminContentStore store,MediaStorage files,PublicContentService views,ObjectMapper json) {this.store=store;this.files=files;this.views=views;this.json=json;}
    public static Class<? extends ContentFields> type(String type) {
        return switch(type){case "projects"->ProjectContent.class;case "blog"->Blogpost.class;default->throw new AdminProblem(404,"not_found");};
    }
    public static void paging(int page,int size) { if(page<0||page>10000||size<1||size>50)throw new AdminProblem(400,"invalid_page"); }
    public Page<? extends ContentFields> list(String type,String status,int page,int size) {
        paging(page,size);if(status!=null&&!Set.of("draft","published","legacy").contains(status))throw new AdminProblem(400,"invalid_status");
        var model=type(type);return new Page<>(store.list(model,status,page,size),page,size,store.count(model,status));
    }
    public ContentFields get(String type,String id) {
        if(id==null||!id.matches("[a-z0-9][a-z0-9-]{0,79}"))throw new AdminProblem(404,"not_found");
        ContentFields c=store.get(type(type),id);if(c==null)throw new AdminProblem(404,"not_found");return c;
    }
    public ContentFields create(String type,ContentEdit input) {
        var model=type(type);ContentFields c=newInstance(model);input.apply(c);
        c.id=UUID.randomUUID().toString();if(c.slug==null)c.slug=c.id;c.status="draft";c.version=1L;c.updatedAt=Instant.now();
        validate(type,c,false);store.ensureSlugIndex(model);
        if(store.slugTaken(model,c.slug,c.id))throw new AdminProblem(409,"slug_conflict");return store.insert(c);
    }
    public ContentFields save(String type,String id,ContentEdit input,long version) {
        ContentFields current=get(type,id);expected(current,version);
        if("published".equals(current.status))throw new AdminProblem(409,"withdraw_before_editing");
        ContentFields changed=json.convertValue(current,current.getClass());input.apply(changed);changed.status="draft";
        validate(type,changed,false);store.ensureSlugIndex(type(type));
        if(store.slugTaken(type(type),changed.slug,id))throw new AdminProblem(409,"slug_conflict");
        return write(changed,version);
    }
    public ContentFields publish(String type,String id,long version) {
        ContentFields current=get(type,id);expected(current,version);
        ContentFields changed=json.convertValue(current,current.getClass());
        if(changed.coverImageId!=null && changed.coverAlt.isEmpty()) {
            var media=store.media(changed.coverImageId);if(media!=null)changed.coverAlt=new HashMap<>(media.alt);
        }
        validate(type,changed,true);store.ensureSlugIndex(type(type));
        if(store.slugTaken(type(type),changed.slug,id))throw new AdminProblem(409,"slug_conflict");
        changed.status="published";if(changed.publishedAt==null||changed.publishedAt.isAfter(Instant.now()))changed.publishedAt=Instant.now();
        return write(changed,version);
    }
    public ContentFields withdraw(String type,String id,long version) {
        ContentFields changed=get(type,id);expected(changed,version);changed=json.convertValue(changed,changed.getClass());
        changed.status="draft";return write(changed,version); // Keep first publication timestamp.
    }
    private ContentFields write(ContentFields changed,long version) {
        changed.version=version+1;changed.updatedAt=Instant.now();
        if(!store.replace(changed,version))throw new AdminProblem(409,"version_conflict");return changed;
    }
    private void expected(ContentFields current,long version) { if(AdminContentStore.version(current.version)!=version)throw new AdminProblem(409,"version_conflict"); }
    private void validate(String type,ContentFields c,boolean publish) {
        boolean selected=c instanceof ProjectContent && store.selection().projectIds.contains(c.id);
        ContentValidator.validate(c,type,publish,selected,store,files);
    }
    private ContentFields newInstance(Class<? extends ContentFields> type) {return type==ProjectContent.class?new ProjectContent():new Blogpost();}
    public HomepageSelection selection() {return store.selection();}
    public HomepageSelection selection(List<String> ids,long version) {
        AdminProblem.require(ids!=null&&ids.size()<=3&&new HashSet<>(ids).size()==ids.size(),"invalid_homepage_selection");
        for(String id:ids) {ContentFields p=get("projects",id);if("published".equals(p.status))ContentValidator.validate(p,"projects",true,true,store,files);}
        var changed=new HomepageSelection();changed.projectIds=List.copyOf(ids);changed.version=version+1;
        if(!store.replaceSelection(changed,version))throw new AdminProblem(409,"version_conflict");return changed;
    }
    public Preview preview(String type,String id,String language) {
        if(!Set.of("de","en").contains(language))throw new AdminProblem(400,"invalid_language");
        String lang=language;ContentFields c=get(type,id);
        JsonNode content=json.valueToTree(c instanceof ProjectContent p?views.view(p,lang):views.blogView((Blogpost)c,lang,true));
        protectedUrls(content);
        Map<String,JsonNode> media=new LinkedHashMap<>();Set<String> ids=new LinkedHashSet<>(c.mediaIds);if(c.coverImageId!=null)ids.add(c.coverImageId);
        for(String mediaId:ids) {MediaAsset asset=store.media(mediaId);if(asset==null)continue;
            var data=json.createObjectNode();data.put("smallUrl","/api/admin/media/"+mediaId+"/small");data.put("largeUrl","/api/admin/media/"+mediaId+"/large");
            data.set("alt",json.valueToTree(asset.alt));media.put(mediaId,data);}
        return new Preview(content,media,c.status,AdminContentStore.version(c.version));
    }
    private void protectedUrls(JsonNode node) {
        if(node instanceof ObjectNode object) {
            var fields=new ArrayList<String>();object.fieldNames().forEachRemaining(fields::add);
            for(String field:fields) {JsonNode value=object.get(field);if(value.isTextual())object.put(field,value.asText().replace("/api/media/","/api/admin/media/"));else protectedUrls(value);}
        } else if(node instanceof ArrayNode array)for(JsonNode value:array)protectedUrls(value);
    }
}
