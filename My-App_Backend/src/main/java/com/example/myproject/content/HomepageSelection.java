package com.example.myproject.content;

import java.util.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** One atomic selection document; legacy homeOrder remains a read-only fallback until explicitly configured. */
@Document("content_settings")
public class HomepageSelection {
    public static final String ID="homepage-projects";
    @Id public String id=ID;
    public List<String> projectIds=new ArrayList<>();
    public Long version;
}
