package com.example.myproject.content;
import com.example.myproject.model.Blogpost;
import java.time.Instant;
import java.util.*;
import java.text.NumberFormat;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Service
public class PublicContentService {
    private final ContentStore store;
    public PublicContentService(ContentStore store){this.store=store;}
    public record Image(String id,String smallUrl,String largeUrl,int smallWidth,int width,int height,String alt){}
    public record Project(String id,String slug,String category,Image image,List<String> dates,ProjectContent.Duration duration,
        Integer investmentChf,Integer areaM2,Integer apartments,Integer thermostats,Integer doors,Integer complexity,
        String title,String description,String location,List<String> tags,String challenge,String result){}
    public record Blog(String id,String slug,Map<String,String> title,Map<String,String> excerpt,Map<String,String> description,List<String> categories,Image image,Instant publishedAt,List<String> mediaIds){}
    public record Page<T>(List<T> items,int page,int size,long totalElements,int totalPages){}
    public static String language(String lang){if(!List.of("de","en").contains(lang))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid language");return lang;}
    static String text(Map<String,String> values,String lang){return values==null?"":values.getOrDefault(lang,values.getOrDefault("de",""));}
    static <T extends ContentFields> T requirePublic(T content){if(content==null||!content.published(Instant.now()))throw new ResponseStatusException(HttpStatus.NOT_FOUND);return content;}
    public Image image(ContentFields content,String lang){
        if(content.coverImageId==null)return null;var media=store.media(content.coverImageId);if(media==null)return null;
        var large=media.variants.get("large");if(large==null||!media.variants.containsKey("small"))return null;
        return new Image(media.id,"/api/media/"+media.id+"/small","/api/media/"+media.id+"/large",media.variants.get("small").width(),large.width(),large.height(),text(content.coverAlt.isEmpty()?media.alt:content.coverAlt,lang));
    }
    public Project view(ProjectContent p,String lang){
        String description=render(p.description,p,lang),challenge=render(p.challenge,p,lang),result=render(p.result,p,lang);
        return new Project(p.id,p.slug,p.categories.isEmpty()?"":p.categories.get(0),image(p,lang),p.dates,p.duration,p.investmentChf,p.areaM2,p.apartments,p.thermostats,p.doors,p.complexity,
            text(p.title,lang),description,text(p.location,lang),p.tags.getOrDefault(lang,p.tags.getOrDefault("de",List.of())),challenge,result);
    }
    String render(Map<String,String> template,ProjectContent p,String lang){
        var number=NumberFormat.getIntegerInstance(Locale.forLanguageTag("de".equals(lang)?"de-CH":"en-GB"));String value=text(template,lang);
        var fields=new HashMap<String,String>();fields.put("area",p.areaM2==null?"":p.areaM2.toString());fields.put("apartments",p.apartments==null?"":p.apartments.toString());fields.put("thermostats",p.thermostats==null?"":p.thermostats.toString());fields.put("investment",p.investmentChf==null?"":"CHF "+number.format(p.investmentChf));
        for(var entry:fields.entrySet())value=value.replace("{{"+entry.getKey()+"}}",entry.getValue());return value;
    }
    public Page<Project> projects(String category,int page,int size,boolean featured,String lang){
        language(lang);validatePaging(page,size);if(category!=null&&!category.isBlank()&&!ContentCategories.valid("projects",category))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        int limit=featured?3:size;var list=store.list(ProjectContent.class,category,page,limit,featured).stream().filter(p->p.published(Instant.now())).map(p->view(p,lang)).toList();
        long total=store.count(ProjectContent.class,category,featured);return new Page<>(list,page,limit,total,(int)Math.ceil((double)total/limit));
    }
    public Project project(String id,String lang){return view(requirePublic(store.byId(ProjectContent.class,id)),language(lang));}
    public Page<Blog> blogs(String category,int page,int size,String lang){
        language(lang);validatePaging(page,size);if(category!=null&&!category.isBlank()&&!ContentCategories.valid("blog",category))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var list=store.list(Blogpost.class,category,page,size,false).stream().filter(b->b.published(Instant.now())).map(b->blogView(b,lang,false)).toList();
        long total=store.count(Blogpost.class,category,false);return new Page<>(list,page,size,total,(int)Math.ceil((double)total/size));
    }
    public Blog blogView(Blogpost b,String lang,boolean full){return new Blog(b.id,b.slug,b.title,b.excerpt,full?b.description:null,b.categories,image(b,lang),b.publishedAt,full?List.copyOf(b.mediaIds):null);}
    public Blog blog(String slug,String lang){return blogView(requirePublic(store.bySlug(Blogpost.class,slug)),language(lang),true);}
    public Blogpost legacyBlog(String id){return requirePublic(store.byId(Blogpost.class,id));}
    public List<Blogpost> legacyBlogs(){return store.list(Blogpost.class,null,0,24,false).stream().filter(b->b.published(Instant.now())).toList();}
    static void validatePaging(int page,int size){if(page<0||page>10000||size<1||size>24)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid page size");}
}
