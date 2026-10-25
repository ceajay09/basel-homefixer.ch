package com.example.myproject.content;
import java.util.*;
public final class ContentCategories {
    public record Category(String id,String label){}
    private static final Map<String,List<String>> LABELS=Map.of(
        "projects",List.of("kitchen|Küchen|Kitchens","electrical|Elektrik|Electrical","windows|Fenster|Windows","balcony|Balkon|Balcony","heating|Heizung|Heating"),
        "blog",List.of("renovation|Renovation|Renovation","kitchen|Küche|Kitchen","energy|Energie|Energy","smart-home|Smart Home|Smart home"));
    public static List<Category> list(String type,String lang){return LABELS.getOrDefault(type,List.of()).stream().map(s->{var a=s.split("\\|");return new Category(a[0],a["de".equals(lang)?1:2]);}).toList();}
    public static boolean valid(String type,String id){return list(type,"de").stream().anyMatch(c->c.id().equals(id));}
}
