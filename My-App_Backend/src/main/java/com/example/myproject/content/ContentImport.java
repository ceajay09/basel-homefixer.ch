package com.example.myproject.content;
import com.example.myproject.model.Blogpost;
import com.fasterxml.jackson.databind.*;
import java.nio.file.*;
import java.util.*;
import java.time.*;
import java.io.IOException;

/** Explicit local operation; never a Spring bean, HTTP endpoint or startup runner. */
public class ContentImport {
    public static class Bundle {
        public int version=1;
        public List<ProjectContent> projects=new ArrayList<>();
        public List<Blogpost> blogs=new ArrayList<>();
        public Map<String,Source> media=new HashMap<>();
    }
    public record Source(String filename,Map<String,String> alt){}
    public interface Target {
        ContentFields byId(Class<? extends ContentFields> type,String id);
        ContentFields bySlug(Class<? extends ContentFields> type,String slug);
        MediaAsset media(String id);
        void ensureIndexes();void save(ContentFields content);void saveMedia(MediaAsset media);
    }
    public record Plan(List<ContentFields> contents,List<MediaStorage.Prepared> media){}
    private final ObjectMapper json;private final MediaStorage storage;
    public ContentImport(ObjectMapper json,MediaStorage storage){this.json=json;this.storage=storage;}
    static void require(boolean condition,String message){if(!condition)throw new IllegalArgumentException(message);}
    static void bilingual(Map<String,String> text,String field){require(text!=null&&text.getOrDefault("de","").length()>0&&text.getOrDefault("en","").length()>0,"Missing DE/EN "+field);}
    public Plan prepare(Path manifest,Path sourceRoot,boolean allowPublish) throws IOException {
        require(Files.size(manifest)<=2*1024*1024,"Manifest too large");Bundle bundle=json.readValue(manifest.toFile(),Bundle.class);require(bundle.version==1,"Unknown manifest version");
        require(bundle.projects.size()+bundle.blogs.size()<=1000&&bundle.media.size()<=1000,"Import too large");
        var media=new HashMap<String,MediaStorage.Prepared>();for(var entry:bundle.media.entrySet()){bilingual(entry.getValue().alt(),"media alt");media.put(entry.getKey(),storage.prepare(sourceRoot,entry.getValue().filename(),entry.getValue().alt()));}
        var contents=new ArrayList<ContentFields>();contents.addAll(bundle.projects);contents.addAll(bundle.blogs);var ids=new HashSet<String>();var slugs=new HashSet<String>();var homeOrders=new HashSet<Integer>();
        for(ContentFields content:contents){String type=content instanceof ProjectContent?"projects":"blog";
            require(content.id!=null&&content.id.matches("[a-z0-9][a-z0-9-]{0,79}"),"Invalid content id");
            require(ids.add(type+":"+content.id),"Duplicate id");require(content.slug!=null&&content.slug.matches("[a-z0-9][a-z0-9-]{0,119}")&&slugs.add(type+":"+content.slug),"Invalid/duplicate slug");
            bilingual(content.title,"title");bilingual(content.description,"description");bilingual(content.excerpt,"excerpt");
            require(content.categories!=null&&!content.categories.isEmpty()&&content.categories.stream().allMatch(c->ContentCategories.valid(type,c)),"Invalid category");
            require(Set.of("draft","published").contains(content.status==null?"":content.status),"Explicit draft/published required");
            require(!"published".equals(content.status)||allowPublish&&content.publishedAt!=null,"Publication requires explicit flag and date");
            require(content.coverImageId!=null&&media.containsKey(content.coverImageId),"Missing cover media source");
            String alias=content.coverImageId;content.coverImageId=media.get(alias).asset().id;
            if(content.coverAlt==null||content.coverAlt.isEmpty())content.coverAlt=new HashMap<>(media.get(alias).asset().alt);bilingual(content.coverAlt,"cover alt");
            var references=new ArrayList<String>();for(String ref:content.mediaIds){require(media.containsKey(ref),"Unknown inline media");references.add(media.get(ref).asset().id);}content.mediaIds=references;
            if(content instanceof ProjectContent p){
                bilingual(p.location,"location");require(p.dates!=null&&!p.dates.isEmpty(),"Missing project date");for(String date:p.dates)YearMonth.parse(date);
                if(p.duration!=null)require(p.duration.min()>0&&p.duration.max()>=p.duration.min()&&Set.of("months","weeks").contains(p.duration.unit()),"Invalid duration");
                for(Integer metric:Arrays.asList(p.areaM2,p.investmentChf,p.apartments,p.thermostats,p.doors,p.complexity))require(metric==null||metric>=0,"Negative metric");
                require(p.complexity==null||p.complexity<=5,"Invalid complexity");
                require(p.homeOrder==null||p.homeOrder>=0&&homeOrders.add(p.homeOrder),"Duplicate/invalid homepage order");
                for(String language:List.of("de","en")){require(p.tags.containsKey(language),"Missing tags language");if(p.homeOrder!=null){bilingual(p.challenge,"challenge");bilingual(p.result,"result");}
                    String all=p.description.get(language)+p.challenge.getOrDefault(language,"")+p.result.getOrDefault(language,"");
                    for(String token:List.of("area","apartments","thermostats","investment")){boolean present=switch(token){case "area"->p.areaM2!=null;case "apartments"->p.apartments!=null;case "thermostats"->p.thermostats!=null;default->p.investmentChf!=null;};require(!all.contains("{{"+token+"}}")||present,"Missing template metric");}
                    require(!all.replaceAll("\\{\\{(area|apartments|thermostats|investment)\\}\\}","").contains("{{"),"Unknown text token");
                }
            }
        }
        return new Plan(contents,List.copyOf(media.values()));
    }
    public void apply(Plan plan,Target target,boolean updateDrafts) throws IOException {
        // Preflight the entire batch before creating files or writing any document.
        for(ContentFields item:plan.contents()){
            ContentFields existing=target.byId(item.getClass(),item.id),sameSlug=target.bySlug(item.getClass(),item.slug);
            require(sameSlug==null||sameSlug.id.equals(item.id),"Slug already belongs to another id");
            if(existing!=null&&!json.valueToTree(existing).equals(json.valueToTree(item)))require((existing.version==null||existing.version==0)&&updateDrafts&&"draft".equals(existing.status)&&"draft".equals(item.status),"Existing content differs; only explicit draft updates allowed");
        }
        for(var prepared:plan.media()){var existing=target.media(prepared.asset().id);require(existing==null||Objects.equals(existing.sourceSha256,prepared.asset().sourceSha256),"Media id conflict");}
        target.ensureIndexes();
        for(var prepared:plan.media()){storage.put(prepared);if(target.media(prepared.asset().id)==null)target.saveMedia(prepared.asset());}
        for(ContentFields item:plan.contents()){var existing=target.byId(item.getClass(),item.id);if(existing==null||!json.valueToTree(existing).equals(json.valueToTree(item)))target.save(item);}
    }
}
