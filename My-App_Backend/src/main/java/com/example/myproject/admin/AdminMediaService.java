package com.example.myproject.admin;

import com.example.myproject.content.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminMediaService {
    private final AdminContentStore store;
    private final ContentStore publicStore;
    private final MediaStorage storage;
    private final int maxBytes;
    private final Semaphore processing;
    public AdminMediaService(AdminContentStore store,ContentStore publicStore,MediaStorage storage,
        @Value("${ADMIN_MEDIA_MAX_BYTES:15728640}") int maxBytes,
        @Value("${ADMIN_MEDIA_CONCURRENT_UPLOADS:1}") int concurrent) {
        if(maxBytes<1024||maxBytes>MediaStorage.MAX_BYTES||concurrent<1||concurrent>4)throw new IllegalArgumentException("Invalid admin media limits");
        this.store=store;this.publicStore=publicStore;this.storage=storage;this.maxBytes=maxBytes;this.processing=new Semaphore(concurrent);
    }
    public MediaAsset get(String id) {
        if(!MediaStorage.validId(id))throw new AdminProblem(404,"not_found");
        var asset=store.media(id);if(asset==null)throw new AdminProblem(404,"not_found");return asset;
    }
    public AdminContentService.Page<MediaAsset> list(int page,int size) {
        AdminContentService.paging(page,size);return new AdminContentService.Page<>(store.mediaList(page,size),page,size,store.mediaCount());
    }
    public MediaAsset upload(MultipartFile file,Map<String,String> alt) {
        if(!processing.tryAcquire())throw new AdminProblem(429,"upload_busy");
        try {
            if(file==null||file.isEmpty()||file.getSize()>maxBytes)throw new AdminProblem(413,"image_too_large_or_empty");
            Map<String,String> texts=alt==null?Map.of():alt;ContentValidator.text(texts,1000,false,"media_alt");
            MediaStorage.Prepared prepared;
            try(var input=file.getInputStream()) {
                byte[] bytes=input.readNBytes(maxBytes+1);
                if(bytes.length>maxBytes)throw new AdminProblem(413,"image_too_large");
                prepared=storage.prepare(bytes,texts); // Client filename and claimed MIME type never determine paths or format.
            } catch(IOException invalid){throw new AdminProblem(422,"invalid_image");}
            try{storage.put(prepared);}catch(IOException unavailable){throw new AdminProblem(503,"media_storage_unavailable");}
            MediaAsset existing=store.media(prepared.asset().id);
            if(existing!=null) {
                if(!Objects.equals(existing.sourceSha256,prepared.asset().sourceSha256))throw new AdminProblem(409,"media_id_conflict");
                return existing;
            } // Duplicate bytes never overwrite existing shared alt texts.
            prepared.asset().version=1L;
            try{return store.insertMedia(prepared.asset());}
            catch(DuplicateKeyException concurrent){return get(prepared.asset().id);}
        } finally {processing.release();}
    }
    public MediaAsset alt(String id,Map<String,String> texts,long version) {
        var asset=get(id);if(AdminContentStore.version(asset.version)!=version)throw new AdminProblem(409,"version_conflict");
        ContentValidator.text(texts,1000,publicStore.publicReference(id),"media_alt");
        if(!store.replaceAlt(id,Map.copyOf(texts),version))throw new AdminProblem(409,"version_conflict");return get(id);
    }
}
