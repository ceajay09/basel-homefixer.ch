package com.example.myproject.admin;

import com.example.myproject.content.*;
import java.net.URI;
import java.util.Map;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/media")
public class AdminMediaController {
    private final AdminMediaService service;
    private final MediaStorage storage;
    public AdminMediaController(AdminMediaService service,MediaStorage storage){this.service=service;this.storage=storage;}
    @GetMapping public ResponseEntity<?> list(@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.list(page,size));
    }
    @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart("file")MultipartFile file,@RequestPart(value="alt",required=false)Map<String,String> alt) {
        var c=service.upload(file,alt);return ResponseEntity.created(URI.create("/api/admin/media/"+c.id)).cacheControl(CacheControl.noStore()).eTag(Long.toString(AdminContentStore.version(c.version))).body(c);
    }
    @GetMapping("/{id}") public ResponseEntity<?> get(@PathVariable String id){var c=service.get(id);return AdminHeaders.response(c,c.version);}
    public record Alt(Map<String,String> alt){}
    @PutMapping(path="/{id}/alt",consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> alt(@PathVariable String id,@RequestBody Alt input,@RequestHeader(value="If-Match",required=false)String expected) {
        var c=service.alt(id,input.alt(),AdminHeaders.expected(expected));return AdminHeaders.response(c,c.version);
    }
    @GetMapping("/{id}/{variant}") public ResponseEntity<Resource> image(@PathVariable String id,@PathVariable String variant) {
        if(!java.util.Set.of("small","large").contains(variant))throw new AdminProblem(404,"not_found");
        var media=service.get(id);if(!media.variants.containsKey(variant))throw new AdminProblem(404,"not_found");
        try{return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).cacheControl(CacheControl.noStore()).header("X-Content-Type-Options","nosniff").body(new FileSystemResource(storage.publicFile(id,variant)));}
        catch(java.io.IOException missing){throw new AdminProblem(404,"not_found");}
    }
}
