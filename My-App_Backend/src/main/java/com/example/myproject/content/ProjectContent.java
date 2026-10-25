package com.example.myproject.content;
import java.util.*;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("projects")
public class ProjectContent extends ContentFields {
    public Map<String,String> location = new HashMap<>(), challenge = new HashMap<>(), result = new HashMap<>();
    public Map<String,List<String>> tags = new HashMap<>();
    public List<String> dates = new ArrayList<>();
    public Duration duration;
    public Integer investmentChf, areaM2, apartments, thermostats, doors, complexity;
    public Integer homeOrder;
    public record Duration(int min, int max, String unit) {}
}
