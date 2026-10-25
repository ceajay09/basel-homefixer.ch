package com.example.myproject.admin;

import com.example.myproject.content.*;
import java.util.*;

/** Request data only; identity, status, timestamps, homepage order and version are server controlled. */
public record ContentEdit(String slug, Map<String,String> title, Map<String,String> excerpt,
    Map<String,String> description, List<String> categories, String coverImageId, Map<String,String> coverAlt,
    List<String> mediaIds, Map<String,String> location, Map<String,String> challenge, Map<String,String> result,
    Map<String,List<String>> tags, List<String> dates, ProjectContent.Duration duration,
    Integer investmentChf, Integer areaM2, Integer apartments, Integer thermostats, Integer doors, Integer complexity) {
    public void apply(ContentFields target) {
        target.slug=slug;
        target.title=map(title); target.excerpt=map(excerpt); target.description=map(description);
        target.categories=list(categories); target.coverImageId=coverImageId; target.coverAlt=map(coverAlt); target.mediaIds=list(mediaIds);
        if(target instanceof ProjectContent p) {
            p.location=map(location);p.challenge=map(challenge);p.result=map(result);
            p.tags=tags==null?new HashMap<>():new HashMap<>(tags); p.dates=list(dates);p.duration=duration;
            p.investmentChf=investmentChf;p.areaM2=areaM2;p.apartments=apartments;p.thermostats=thermostats;p.doors=doors;p.complexity=complexity;
        }
    }
    static Map<String,String> map(Map<String,String> value) { return value==null?new HashMap<>():new HashMap<>(value); }
    static <T> List<T> list(List<T> value) { return value==null?new ArrayList<>():new ArrayList<>(value); }
}
