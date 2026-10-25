package com.example.myproject.admin;

import com.example.myproject.auth.*;
import com.example.myproject.config.SecurityConfig;
import com.example.myproject.content.*;
import com.example.myproject.model.Account;
import com.example.myproject.repository.AccountRepository;
import com.example.myproject.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.*;
import java.util.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.mockito.Mockito.*;

/** Opt-in local browser fixture. No MongoDB, mail, NAS, real accounts or production config. Not packaged in the application JAR. */
public final class AdminBrowserFixture {
    @Configuration(proxyBeanMethods=false)
    @EnableAutoConfiguration(exclude={MongoAutoConfiguration.class,MongoDataAutoConfiguration.class,MongoRepositoriesAutoConfiguration.class})
    @Import({SecurityConfig.class,AuthController.class,AuthExceptionHandler.class,AccountService.class,
        AdminContentController.class,AdminMediaController.class,AdminExceptionHandler.class,AdminMediaConfiguration.class,
        AdminContentService.class,AdminMediaService.class,ContentConfiguration.class,PublicContentService.class,PublicContentController.class,PublicMediaController.class})
    static class Config {
        @Bean MemoryAdminStore adminStore(ObjectMapper json){return new MemoryAdminStore(json);}
        @Bean MemoryPublicStore publicStore(MemoryAdminStore store){return new MemoryPublicStore(store);}
        @Bean AuthAccountIndexes indexes(){return mock(AuthAccountIndexes.class);}
        @Bean AccountRepository accounts(){
            var repo=mock(AccountRepository.class);var account=new Account();account.setID("browser-admin");account.setEmail("browser@example.invalid");
            account.setPassword(new BCryptPasswordEncoder(4).encode("Isolated-browser-only-123"));account.setAuthRevision(UUID.randomUUID().toString());account.setAdmin(true);
            when(repo.findById(account.getID())).thenReturn(Optional.of(account));when(repo.findByEmailIgnoreCase(account.getEmail())).thenReturn(List.of(account));return repo;
        }
    }
    public static void main(String[] args) throws Exception {
        if(args.length!=1 && args.length!=3)throw new IllegalArgumentException("Supply temporary endpoint output file");
        Path root=Files.createTempDirectory("basel-admin-browser-");
        var app=new SpringApplication(Config.class);
        var context=(ServletWebServerApplicationContext)app.run("--server.address=127.0.0.1","--server.port=0",
            "--spring.config.location=optional:classpath:/isolated-browser-only.properties","--logging.level.root=ERROR",
            "--AUTH_JWT_SECRET_BASE64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=","--AUTH_TOKEN_TTL_SECONDS=900",
            "--AUTH_PUBLIC_REGISTRATION_ENABLED=false","--CONTENT_MEDIA_ROOT="+root);
        if(args.length==3) {
            var mapper=context.getBean(ObjectMapper.class);var files=context.getBean(MediaStorage.class);var data=context.getBean(MemoryAdminStore.class);
            var importer=new ContentImport(mapper,files);var plan=importer.prepare(Path.of(args[1]),Path.of(args[2]),false);
            if(plan.contents().stream().anyMatch(c->!"draft".equals(c.status)||c.publishedAt!=null))throw new IllegalArgumentException("Only unpublished drafts in browser fixture");
            importer.apply(plan,new ContentImport.Target(){
                public ContentFields byId(Class<? extends ContentFields> type,String id){return data.get(type,id);}
                public ContentFields bySlug(Class<? extends ContentFields> type,String slug){return data.list(type,null,0,1000).stream().filter(c->slug.equals(c.slug)).findFirst().orElse(null);}
                public MediaAsset media(String id){return data.media(id);}public void ensureIndexes(){}
                public void save(ContentFields c){data.insert(c);}public void saveMedia(MediaAsset m){data.insertMedia(m);}
            },false);
            System.out.println("Isolated drafts imported: "+plan.contents().size()+". None published.");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(()->{try{
            Path expected=Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
            if(!root.toAbsolutePath().normalize().startsWith(expected)||!root.getFileName().toString().startsWith("basel-admin-browser-"))return;
            try(var files=Files.walk(root)){for(Path file:files.sorted(Comparator.reverseOrder()).toList())Files.deleteIfExists(file);}
        }catch(Exception ignored){}}));
        Files.writeString(Path.of(args[0]),"http://127.0.0.1:"+context.getWebServer().getPort());
    }
}
