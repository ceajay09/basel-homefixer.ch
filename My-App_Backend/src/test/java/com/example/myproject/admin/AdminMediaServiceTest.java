package com.example.myproject.admin;

import com.example.myproject.content.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminMediaServiceTest {
    @TempDir Path temp;
    byte[] image()throws Exception{var out=new ByteArrayOutputStream();ImageIO.write(new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB),"png",out);return out.toByteArray();}
    @Test void concurrentDecodingIsBoundedAndPermitIsReleasedAfterFailures()throws Exception {
        var store=new MemoryAdminStore(new ObjectMapper().findAndRegisterModules());var service=new AdminMediaService(store,new MemoryPublicStore(store),new MediaStorage(temp),1024,1);
        var entered=new CountDownLatch(1);var release=new CountDownLatch(1);
        var blocking=new MockMultipartFile("file","ignored.jpg","image/jpeg",image()) {
            @Override public InputStream getInputStream()throws IOException {
                entered.countDown();try{if(!release.await(5,TimeUnit.SECONDS))throw new IOException("Test timed out");}catch(InterruptedException interrupted){Thread.currentThread().interrupt();throw new IOException(interrupted);}
                return super.getInputStream();
            }
        };
        var executor=Executors.newSingleThreadExecutor();
        try {
            var pending=executor.submit(()->service.upload(blocking,Map.of()));assertTrue(entered.await(3,TimeUnit.SECONDS));
            var busy=assertThrows(AdminProblem.class,()->service.upload(new MockMultipartFile("file",image()),Map.of()));assertEquals(429,busy.getStatusCode().value());
            release.countDown();assertNotNull(pending.get(5,TimeUnit.SECONDS));
            assertEquals(422,assertThrows(AdminProblem.class,()->service.upload(new MockMultipartFile("file","<html>".getBytes()),Map.of())).getStatusCode().value());
            assertNotNull(service.upload(new MockMultipartFile("file",image()),Map.of()));
        } finally {release.countDown();executor.shutdownNow();}
    }
    @Test void streamLimitDoesNotTrustReportedSizeAndSharedAltTextCannotBeBlanked()throws Exception {
        var store=new MemoryAdminStore(new ObjectMapper().findAndRegisterModules());var publicStore=new MemoryPublicStore(store);var files=new MediaStorage(temp);
        var service=new AdminMediaService(store,publicStore,files,1024,1);
        var falseSize=new MockMultipartFile("file",new byte[2048]){@Override public long getSize(){return 1;}};
        assertEquals(413,assertThrows(AdminProblem.class,()->service.upload(falseSize,Map.of())).getStatusCode().value());
        var asset=service.upload(new MockMultipartFile("file",image()),Map.of("de","Bild","en","Photo"));
        var content=new ProjectContent();content.id="published";content.status="published";content.publishedAt=java.time.Instant.now().minusSeconds(1);content.coverImageId=asset.id;store.insert(content);
        assertEquals(422,assertThrows(AdminProblem.class,()->service.alt(asset.id,Map.of(),1)).getStatusCode().value());
        var returned=service.upload(new MockMultipartFile("file",image()),Map.of("de","Andere","en","Other"));assertEquals("Bild",returned.alt.get("de"));
        assertTrue(Files.exists(files.publicFile(asset.id,"large")));assertTrue(publicStore.anyReference(asset.id));
    }
}
