package com.example.myproject.config;

import com.example.myproject.model.Blogpost;
import com.example.myproject.repository.BlogRepository;

import com.example.myproject.service.BlogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Initializes the application by preloading necessary data.
 * Ensures that blog and account services are ready for use once the application starts.
 */
@Component
public class ApplicationInitializer implements CommandLineRunner {

    private final BlogService blogService;

    private static final Logger logger = LogManager.getLogger(ApplicationInitializer.class);
    private final MongoTemplate mongoTemplate;
    private final BlogRepository blogRepository;

    @Autowired
    public ApplicationInitializer(BlogService blogService, MongoTemplate mongoTemplate, BlogRepository blogRepository) {
        this.blogService = blogService;

        this.mongoTemplate = mongoTemplate;
        this.blogRepository = blogRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing application data...");
        try {
            Document commandResult = mongoTemplate.executeCommand("{ ping: 1 }");
            logger.info("MongoDB Ping Response: " + commandResult.toString());
        } catch (Exception e) {
            logger.error("Failed to connect to MongoDB", e);
        }
//        createBlogpost();
        this.blogService.getAllBlogposts(true);
        // Account data is loaded only on authenticated requests; no startup account dump.
        logger.info("Application data initialized successfully.");
    }
    
    public void createBlogpost(){
        Blogpost newBlog = new Blogpost(); 
        Map<String, String> testDescription = new HashMap<>();
        testDescription.put("en", "English Desc");
        testDescription.put("de", "Deutsche Desc");
        newBlog.setDescription(testDescription);
        this.blogRepository.save(newBlog);
        logger.info("New blogpost " + newBlog.getId() + " created and saved in DB");
    }
}
