package com.example.myproject.content;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.core.io.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
public class PublicMediaController {
    private final ContentStore store;private final MediaStorage storage;
    public PublicMediaController(ContentStore store,MediaStorage storage){this.store=store;this.storage=storage;}
    @GetMapping("/api/media/{id}/{variant}") public ResponseEntity<Resource> media(@PathVariable String id,@PathVariable String variant){
        if(!MediaStorage.validId(id)||!java.util.Set.of("small","large").contains(variant)||!store.publicReference(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        var asset=store.media(id);if(asset==null||!asset.variants.containsKey(variant))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        try{return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).cacheControl(CacheControl.noStore()).header("X-Content-Type-Options","nosniff")
            .body(new FileSystemResource(storage.publicFile(id,variant)));}
        catch(java.io.IOException e){throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
    }
}
