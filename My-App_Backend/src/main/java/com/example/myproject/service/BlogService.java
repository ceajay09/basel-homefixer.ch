package com.example.myproject.service;
import com.example.myproject.model.Blogpost;
import com.example.myproject.content.PublicContentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class BlogService {
    private final PublicContentService content;
    public BlogService(PublicContentService content){this.content=content;}
    /** Retained for the existing initializer. No public-content snapshot/cache is populated. */
    public void getAllBlogposts(boolean initialized) {}
    public Queue<Blogpost> getBlogposts(HttpServletRequest request){return new LinkedList<>(content.legacyBlogs());}
    public Blogpost getPublishedById(String id){return content.legacyBlog(id);}
}
