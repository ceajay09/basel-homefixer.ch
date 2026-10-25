package com.example.myproject.content;
import java.util.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("media")
public class MediaAsset {
    @Id public String id;
    public String sourceSha256;
    public Long version;
    public Map<String,String> alt = new HashMap<>();
    public Map<String,Variant> variants = new HashMap<>();
    public record Variant(int width, int height, String sha256) {}
}
