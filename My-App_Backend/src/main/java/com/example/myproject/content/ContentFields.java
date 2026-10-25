package com.example.myproject.content;
import java.time.Instant;
import java.util.*;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentFields {
    @Id public String id;
    public String slug;
    public Map<String,String> title = new HashMap<>(), excerpt = new HashMap<>(), description = new HashMap<>(), coverAlt = new HashMap<>();
    public List<String> categories = new ArrayList<>(), mediaIds = new ArrayList<>();
    public String coverImageId;
    public String status;
    public Instant publishedAt;
    public Long version;
    public Instant updatedAt;
    public boolean published(Instant now) { return "published".equals(status) && publishedAt != null && !publishedAt.isAfter(now); }
    public String getId(){return id;} public void setId(String value){id=value;}
    public Map<String,String> getDescription(){return description;}
    public void setDescription(Map<String,String> value){description=value;}
}
