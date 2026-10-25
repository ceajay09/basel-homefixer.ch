package com.example.myproject.controller;

import com.example.myproject.model.Blogpost;
import com.example.myproject.model.HomepageConfig;

import com.example.myproject.service.BlogService;
import com.example.myproject.service.HomepageConfigService;
import com.example.myproject.service.PDFService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;

import java.util.Queue;

/** Public legacy blog, homepage and PDF integrations. Authentication is handled by AuthController. */
@RestController
@CrossOrigin(origins = { "https://basel-homefixer.ch", "http://localhost:5173" })
@PropertySource("classpath:application.properties")
public class RequestHandler {

    private final PDFService pdfService;
    private final BlogService blogService;
    private final HomepageConfigService homepageConfigService;

    public RequestHandler(PDFService pdfService,
            BlogService blogService,
            HomepageConfigService homepageConfigService) {

        this.pdfService = pdfService;
        this.blogService = blogService;
        this.homepageConfigService = homepageConfigService;
    }

    @GetMapping(path = "api/blogpost/{id}", produces = "application/json") // TODO: Logger? //TODO: in getblog
                                                                           // umbenennen
    public Blogpost getBlogpostById(@PathVariable String id) {
        return blogService.getPublishedById(id);
    }

    @GetMapping(path = "api/getBlogposts", produces = "application/json") // TODO: Logger? TODO: Errorhandling
    public Queue<Blogpost> getBlogposts(HttpServletRequest request) {
        return blogService.getBlogposts(request);
    }

    @GetMapping(path = "api/downloadPDF/{fileName}", produces = "application/json")
    public ResponseEntity<Resource> downloadPDF(@PathVariable String fileName) throws MalformedURLException {
        return pdfService.downloadPDF(fileName);
    }

    @GetMapping("/api/homepage-config")
    public ResponseEntity<HomepageConfig> getHomepageConfig() throws java.io.IOException {
        try {
            // System.err.println("homepage-config reqeusted");
            HomepageConfig config = homepageConfigService.getHomepageConfig();
            return ResponseEntity.ok(config);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
