package com.example.myproject.content;
import org.springframework.web.bind.annotation.*;
@RestController
public class PublicContentController {
    private final PublicContentService service;
    public PublicContentController(PublicContentService service){this.service=service;}
    @GetMapping("/api/projects") public PublicContentService.Page<PublicContentService.Project> projects(@RequestParam(required=false)String category,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="12")int size,@RequestParam(defaultValue="en")String lang){return service.projects(category,page,size,false,lang);}
    @GetMapping("/api/projects/featured") public PublicContentService.Page<PublicContentService.Project> featured(@RequestParam(defaultValue="en")String lang){return service.projects(null,0,3,true,lang);}
    @GetMapping("/api/projects/by-id/{id}") public PublicContentService.Project project(@PathVariable String id,@RequestParam(defaultValue="en")String lang){return service.project(id,lang);}
    @GetMapping("/api/blog") public PublicContentService.Page<PublicContentService.Blog> blogs(@RequestParam(required=false)String category,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="12")int size,@RequestParam(defaultValue="en")String lang){return service.blogs(category,page,size,lang);}
    @GetMapping("/api/blog/{slug}") public PublicContentService.Blog blog(@PathVariable String slug,@RequestParam(defaultValue="en")String lang){return service.blog(slug,lang);}
    @GetMapping("/api/content/categories") public java.util.List<ContentCategories.Category> categories(@RequestParam String type,@RequestParam(defaultValue="en")String lang){PublicContentService.language(lang);if(!java.util.List.of("projects","blog").contains(type))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST);return ContentCategories.list(type,lang);}
}
