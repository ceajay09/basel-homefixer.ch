package com.example.myproject.admin;

import com.example.myproject.content.*;
import java.net.URI;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminContentController {
    private final AdminContentService service;
    public AdminContentController(AdminContentService service){this.service=service;}
    @GetMapping("/{type:projects|blog}") public ResponseEntity<?> list(@PathVariable String type,@RequestParam(required=false)String status,
        @RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.list(type,status,page,size));
    }
    @PostMapping(path="/{type:projects|blog}",consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@PathVariable String type,@RequestBody ContentEdit input) {
        var c=service.create(type,input);return ResponseEntity.created(URI.create("/api/admin/"+type+"/"+c.id)).cacheControl(CacheControl.noStore()).eTag(c.version.toString()).body(c);
    }
    @GetMapping("/{type:projects|blog}/{id}") public ResponseEntity<?> get(@PathVariable String type,@PathVariable String id) {
        var c=service.get(type,id);return AdminHeaders.response(c,c.version);
    }
    @PutMapping(path="/{type:projects|blog}/{id}",consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@PathVariable String type,@PathVariable String id,@RequestBody ContentEdit input,@RequestHeader(value="If-Match",required=false)String expected) {
        var c=service.save(type,id,input,AdminHeaders.expected(expected));return AdminHeaders.response(c,c.version);
    }
    @PostMapping("/{type:projects|blog}/{id}/publish") public ResponseEntity<?> publish(@PathVariable String type,@PathVariable String id,@RequestHeader(value="If-Match",required=false)String expected) {
        var c=service.publish(type,id,AdminHeaders.expected(expected));return AdminHeaders.response(c,c.version);
    }
    @PostMapping("/{type:projects|blog}/{id}/withdraw") public ResponseEntity<?> withdraw(@PathVariable String type,@PathVariable String id,@RequestHeader(value="If-Match",required=false)String expected) {
        var c=service.withdraw(type,id,AdminHeaders.expected(expected));return AdminHeaders.response(c,c.version);
    }
    @GetMapping("/{type:projects|blog}/{id}/preview") public ResponseEntity<?> preview(@PathVariable String type,@PathVariable String id,@RequestParam(defaultValue="de")String lang) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.preview(type,id,lang));
    }
    @GetMapping("/homepage-projects") public ResponseEntity<?> selection(){var c=service.selection();return AdminHeaders.response(c,c.version);}
    public record Selection(List<String> projectIds){}
    @PutMapping(path="/homepage-projects",consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> selection(@RequestBody Selection input,@RequestHeader(value="If-Match",required=false)String expected) {
        var c=service.selection(input.projectIds(),AdminHeaders.expected(expected));return AdminHeaders.response(c,c.version);
    }
}
