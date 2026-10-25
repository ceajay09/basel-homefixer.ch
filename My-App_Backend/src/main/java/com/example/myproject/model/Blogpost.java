package com.example.myproject.model;
import com.example.myproject.content.ContentFields;
import org.springframework.data.mongodb.core.mapping.Document;
/** Existing collection and IDs retained; absent status never implies publication. */
@Document("blogpost")
public class Blogpost extends ContentFields {}
