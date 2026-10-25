package com.example.myproject.admin;

import com.example.myproject.content.*;
import java.time.YearMonth;
import java.util.*;
import java.util.regex.*;

/** Bounded draft data; complete bilingual facts/media are required only for publication. */
public final class ContentValidator {
    private ContentValidator() {}
    public static void text(Map<String,String> values,int max,boolean required,String field) {
        AdminProblem.require(values!=null && values.size()<=2 && values.keySet().stream().allMatch(k->Set.of("de","en").contains(k)),"invalid_"+field);
        for(String value:values.values())AdminProblem.require(value!=null && value.length()<=max,"invalid_"+field);
        if(required)for(String language:List.of("de","en"))AdminProblem.require(!values.getOrDefault(language,"").isBlank(),"missing_"+field);
    }
    public static void validate(ContentFields c,String type,boolean publish,boolean featured,AdminContentStore store,MediaStorage files) {
        AdminProblem.require(c.slug!=null && c.slug.matches("[a-z0-9][a-z0-9-]{0,119}"),"invalid_slug");
        text(c.title,200,publish,"title");text(c.excerpt,1000,publish,"excerpt");text(c.description,50000,publish,"description");text(c.coverAlt,1000,false,"cover_alt");
        AdminProblem.require(c.categories!=null && c.categories.size()<=5 && new HashSet<>(c.categories).size()==c.categories.size()
            && c.categories.stream().allMatch(category->ContentCategories.valid(type,category)),"invalid_categories");
        if(publish)AdminProblem.require(!c.categories.isEmpty(),"missing_categories");
        AdminProblem.require(c.mediaIds!=null && c.mediaIds.size()<=50 && new HashSet<>(c.mediaIds).size()==c.mediaIds.size(),"invalid_media_references");
        Set<String> references=new HashSet<>(c.mediaIds);if(c.coverImageId!=null)references.add(c.coverImageId);
        if(publish)AdminProblem.require(c.coverImageId!=null,"missing_cover_image");
        for(String id:references) {
            AdminProblem.require(MediaStorage.validId(id),"invalid_media_reference");
            MediaAsset media=store.media(id);AdminProblem.require(media!=null && media.variants.containsKey("small") && media.variants.containsKey("large"),"missing_media");
            if(publish)try{files.publicFile(id,"small");files.publicFile(id,"large");}catch(java.io.IOException missing){throw new AdminProblem(422,"missing_media_file");}
        }
        if(publish && c.coverImageId!=null)text(c.coverAlt.isEmpty()?store.media(c.coverImageId).alt:c.coverAlt,1000,true,"cover_alt");
        if(c instanceof ProjectContent p) {
            text(p.location,250,publish,"location");text(p.challenge,3000,publish&&featured,"challenge");text(p.result,3000,publish&&featured,"result");
            AdminProblem.require(p.dates!=null && p.dates.size()<=10,"invalid_dates");
            if(publish)AdminProblem.require(!p.dates.isEmpty(),"missing_dates");
            for(String date:p.dates)try{YearMonth.parse(date);}catch(RuntimeException badDate){throw new AdminProblem(422,"invalid_dates");}
            if(p.duration!=null)AdminProblem.require(p.duration.min()>0 && p.duration.max()>=p.duration.min() && p.duration.max()<=1200 && p.duration.unit()!=null && Set.of("months","weeks").contains(p.duration.unit()),"invalid_duration");
            for(Integer metric:Arrays.asList(p.investmentChf,p.areaM2,p.apartments,p.thermostats,p.doors,p.complexity))AdminProblem.require(metric==null||metric>=0,"invalid_metric");
            AdminProblem.require(p.complexity==null||p.complexity<=5,"invalid_complexity");
            AdminProblem.require(p.tags!=null && p.tags.size()<=2 && p.tags.keySet().stream().allMatch(k->Set.of("de","en").contains(k)),"invalid_tags");
            for(List<String> tags:p.tags.values())AdminProblem.require(tags!=null && tags.size()<=20 && tags.stream().allMatch(tag->tag!=null&&!tag.isBlank()&&tag.length()<=80),"invalid_tags");
            for(String language:List.of("de","en")) {
                String body=p.description.getOrDefault(language,"")+p.challenge.getOrDefault(language,"")+p.result.getOrDefault(language,"");
                for(String token:List.of("area","apartments","thermostats","investment")) {
                    boolean present=switch(token){case "area"->p.areaM2!=null;case "apartments"->p.apartments!=null;case "thermostats"->p.thermostats!=null;default->p.investmentChf!=null;};
                    AdminProblem.require(!body.contains("{{"+token+"}}")||present,"missing_template_metric");
                }
                AdminProblem.require(!body.replaceAll("\\{\\{(area|apartments|thermostats|investment)\\}\\}","").contains("{{"),"unknown_text_token");
            }
        }
        Pattern inline=Pattern.compile("/api/media/([0-9a-f-]{36})/(small|large)");
        for(String body:c.description.values()) {
            Matcher refs=inline.matcher(body);while(refs.find())AdminProblem.require(references.contains(refs.group(1)),"undeclared_markdown_media");
            if(publish && "blog".equals(type)) {
                Matcher images=Pattern.compile("!\\[([^\\]]*)\\]\\(([^\\s)]+)(?:\\s+\"[^\"]*\")?\\)").matcher(body);
                while(images.find())AdminProblem.require(!images.group(1).isBlank() && inline.matcher(images.group(2)).matches(),"invalid_markdown_image");
            }
        }
    }
}
