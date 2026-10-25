package com.example.myproject.admin;

import com.example.myproject.config.SecurityConfig;
import com.example.myproject.content.*;
import com.example.myproject.model.Account;
import com.example.myproject.repository.AccountRepository;
import com.example.myproject.service.TokenService;
import com.fasterxml.jackson.databind.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,classes=AdminHttpTest.Configuration.class,
    properties={"spring.config.location=optional:classpath:/admin-server-test-only.properties","logging.level.root=ERROR",
        "AUTH_JWT_SECRET_BASE64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=","ADMIN_MEDIA_MAX_BYTES=1048576"})
class AdminHttpTest {
    @org.springframework.context.annotation.Configuration(proxyBeanMethods=false)
    @EnableAutoConfiguration(exclude={MongoAutoConfiguration.class,MongoDataAutoConfiguration.class,MongoRepositoriesAutoConfiguration.class})
    @Import({SecurityConfig.class,AdminContentController.class,AdminMediaController.class,AdminExceptionHandler.class,AdminMediaConfiguration.class,
        AdminContentService.class,AdminMediaService.class,ContentConfiguration.class,PublicContentService.class,PublicContentController.class,PublicMediaController.class})
    static class Configuration {
        @Bean MemoryAdminStore adminStore(ObjectMapper json){return new MemoryAdminStore(json);}
        @Bean MemoryPublicStore publicStore(MemoryAdminStore data){return new MemoryPublicStore(data);}
    }
    static final Path TEMP=temporary();
    static Path temporary(){try{return Files.createTempDirectory("basel-admin-http-");}catch(IOException error){throw new ExceptionInInitializerError(error);}}
    @DynamicPropertySource static void config(DynamicPropertyRegistry registry){registry.add("CONTENT_MEDIA_ROOT",()->TEMP.toString());}
    @AfterAll static void cleanup() throws Exception {
        Path root=TEMP.toAbsolutePath().normalize(),parent=Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
        assertTrue(root.startsWith(parent)&&root.getFileName().toString().startsWith("basel-admin-http-"));
        try(var entries=Files.walk(root)){for(Path path:entries.sorted(Comparator.reverseOrder()).toList())Files.deleteIfExists(path);}
    }
    @LocalServerPort int port;@Autowired ObjectMapper json;@Autowired TokenService tokens;@Autowired MemoryAdminStore data;
    @Autowired MediaStorage files;@MockitoBean AccountRepository accounts;
    final HttpClient http=HttpClient.newHttpClient();String admin,member;
    @BeforeEach void setup(){
        data.clear();Account privileged=account("admin",true),ordinary=account("member",false);
        when(accounts.findById("admin")).thenReturn(Optional.of(privileged));when(accounts.findById("member")).thenReturn(Optional.of(ordinary));
        admin=tokens.issue(privileged).token();member=tokens.issue(ordinary).token();
    }
    Account account(String id,boolean isAdmin){var a=new Account();a.setID(id);a.setPassword(new BCryptPasswordEncoder(4).encode("isolated-password"));a.setAuthRevision(UUID.randomUUID().toString());a.setAdmin(isAdmin);return a;}
    HttpResponse<byte[]> call(String method,String path,Object body,String token,String version) throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path));
        if(token!=null)builder.header("Authorization","Bearer "+token);
        if(version!=null)builder.header("If-Match",version);
        if(body!=null)builder.header("Content-Type","application/json");
        return http.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofByteArray(json.writeValueAsBytes(body))).build(),HttpResponse.BodyHandlers.ofByteArray());
    }
    JsonNode node(HttpResponse<byte[]> result)throws Exception{return json.readTree(result.body());}
    String etag(HttpResponse<?> response){return response.headers().firstValue("ETag").orElseThrow();}
    byte[] image()throws Exception {var out=new ByteArrayOutputStream();ImageIO.write(new BufferedImage(20,30,BufferedImage.TYPE_INT_RGB),"png",out);return out.toByteArray();}
    HttpResponse<byte[]> upload(byte[] bytes,String filename,String token)throws Exception {
        String boundary="isolated-test-boundary";var body=new ByteArrayOutputStream();
        body.write(("--"+boundary+"\r\nContent-Disposition: form-data; name=\"alt\"\r\nContent-Type: application/json\r\n\r\n{\"de\":\"Bild\",\"en\":\"Photo\"}\r\n--"+boundary+"\r\nContent-Disposition: form-data; name=\"file\"; filename=\""+filename+"\"\r\nContent-Type: image/jpeg\r\n\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        body.write(bytes);body.write(("\r\n--"+boundary+"--\r\n").getBytes());
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/admin/media")).header("Content-Type","multipart/form-data; boundary="+boundary);
        if(token!=null)builder.header("Authorization","Bearer "+token);
        return http.send(builder.POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray())).build(),HttpResponse.BodyHandlers.ofByteArray());
    }
    String media()throws Exception {var result=upload(image(),"photo.jpg",admin);assertEquals(201,result.statusCode(),new String(result.body()));return node(result).get("id").asText();}
    Map<String,Object> edit(String slug,String image,String type){
        var body=new LinkedHashMap<String,Object>();body.put("slug",slug);body.put("title",Map.of("de","Titel","en","Title"));body.put("excerpt",Map.of("de","Vorschau","en","Excerpt"));
        body.put("description",Map.of("de","Beschreibung","en","Description"));body.put("categories",List.of("projects".equals(type)?"kitchen":"renovation"));
        body.put("coverImageId",image);body.put("location",Map.of("de","Basel","en","Basel"));body.put("dates",List.of("2026-06"));
        body.put("challenge",Map.of("de","Ausgangslage","en","Challenge"));body.put("result",Map.of("de","Ergebnis","en","Result"));return body;
    }
    @Test void allAdminReadWritePreviewAndUploadRoutesRequireCurrentAdminPermission() throws Exception {
        for(String token:Arrays.asList(null,"tampered-token",member)) {
            int expected=token==member?403:401;
            for(String path:List.of("/projects","/blog","/projects/existing","/projects/existing/preview","/media","/media/12345678-1234-1234-1234-123456789abc/small","/homepage-projects"))
                assertEquals(expected,call("GET","/api/admin"+path,null,token,null).statusCode(),path);
            for(String type:List.of("projects","blog")) {
                assertEquals(expected,call("POST","/api/admin/"+type,Map.of(),token,null).statusCode());
                assertEquals(expected,call("PUT","/api/admin/"+type+"/existing",Map.of(),token,"\"0\"").statusCode());
                for(String action:List.of("publish","withdraw"))assertEquals(expected,call("POST","/api/admin/"+type+"/existing/"+action,null,token,"\"0\"").statusCode());
            }
            assertEquals(expected,call("PUT","/api/admin/homepage-projects",Map.of("projectIds",List.of()),token,"\"0\"").statusCode());
            assertEquals(expected,call("PUT","/api/admin/media/12345678-1234-1234-1234-123456789abc/alt",Map.of("alt",Map.of()),token,"\"0\"").statusCode());
            assertEquals(expected,upload(image(),"image.png",token).statusCode());
        }
        assertEquals(0,data.count(ProjectContent.class,null));assertEquals(0,data.count(com.example.myproject.model.Blogpost.class,null));assertEquals(0,data.mediaCount());
    }
    @Test void projectsAndBlogHaveExplicitLifecyclePrivatePreviewImagesAndStablePublicationDate() throws Exception {
        String image=media();String publicImage="/api/media/"+image+"/small";
        for(String type:List.of("projects","blog")) {
            var input=edit("same-slug",image,type);input.put("status","published");input.put("id","attacker-id");input.put("publishedAt","2000-01-01T00:00:00Z");
            var created=call("POST","/api/admin/"+type,input,admin,null);assertEquals(201,created.statusCode());
            String id=node(created).get("id").asText(),base="/api/admin/"+type+"/"+id;
            assertNotEquals("attacker-id",id);assertEquals("draft",node(created).get("status").asText());assertTrue(node(created).get("publishedAt")==null||node(created).get("publishedAt").isNull());
            assertEquals(404,call("GET",publicImage,null,null,null).statusCode());
            var preview=call("GET",base+"/preview?lang=en",null,admin,null);assertEquals(200,preview.statusCode());assertTrue(new String(preview.body()).contains("/api/admin/media/"));assertEquals("no-store",preview.headers().firstValue("Cache-Control").orElseThrow());
            assertEquals(200,call("GET","/api/admin/media/"+image+"/small",null,admin,null).statusCode());
            var published=call("POST",base+"/publish",null,admin,etag(created));assertEquals(200,published.statusCode(),new String(published.body()));
            String timestamp=node(published).get("publishedAt").asText();assertFalse(timestamp.startsWith("2000"));
            assertEquals(200,call("GET",publicImage,null,null,null).statusCode());
            String publicItem="projects".equals(type)?"/api/projects/by-id/"+id+"?lang=de":"/api/blog/same-slug?lang=de";
            assertEquals(200,call("GET",publicItem,null,null,null).statusCode());
            assertEquals(409,call("PUT",base,input,admin,etag(published)).statusCode());
            var withdrawn=call("POST",base+"/withdraw",null,admin,etag(published));assertEquals(200,withdrawn.statusCode());
            assertEquals(404,call("GET",publicImage,null,null,null).statusCode());assertEquals(404,call("GET",publicItem,null,null,null).statusCode());
            var saved=call("PUT",base,input,admin,etag(withdrawn));assertEquals(200,saved.statusCode());assertEquals(id,node(saved).get("id").asText());
            var republished=call("POST",base+"/publish",null,admin,etag(saved));assertEquals(200,republished.statusCode());assertEquals(timestamp,node(republished).get("publishedAt").asText());
            assertEquals(200,call("POST",base+"/withdraw",null,admin,etag(republished)).statusCode());
        }
        assertTrue(Files.exists(files.publicFile(image,"small"))); // Withdrawing never deletes shared assets.
    }
    @Test void versionPreconditionsAndUniqueSlugsPreventSilentOverwrites() throws Exception {
        String media=media();var input=edit("unique",media,"projects");var first=call("POST","/api/admin/projects",input,admin,null);String base="/api/admin/projects/"+node(first).get("id").asText();
        assertEquals(409,call("POST","/api/admin/projects",input,admin,null).statusCode());
        assertEquals(428,call("PUT",base,input,admin,null).statusCode());
        assertEquals(400,call("PUT",base,input,admin,"1").statusCode());
        var changed=new LinkedHashMap<>(input);changed.put("title",Map.of("de","Neu","en","New"));
        var saved=call("PUT",base,changed,admin,etag(first));assertEquals(200,saved.statusCode());
        assertEquals(409,call("PUT",base,input,admin,etag(first)).statusCode());assertEquals(409,call("POST",base+"/publish",null,admin,etag(first)).statusCode());
        assertEquals("Neu",node(call("GET",base,null,admin,null)).get("title").get("de").asText());
        var other=new LinkedHashMap<>(input);other.put("slug","other");var second=call("POST","/api/admin/projects",other,admin,null);String secondBase="/api/admin/projects/"+node(second).get("id").asText();
        assertEquals(409,call("PUT",secondBase,input,admin,etag(second)).statusCode());
    }
    @Test void incompleteDraftsSaveButInvalidFactsReferencesAndPublicationAreRejected() throws Exception {
        var draft=call("POST","/api/admin/projects",Map.of(),admin,null);assertEquals(201,draft.statusCode());String base="/api/admin/projects/"+node(draft).get("id").asText();
        assertEquals(422,call("POST",base+"/publish",null,admin,etag(draft)).statusCode());
        assertEquals(400,call("GET",base+"/preview?lang=fr",null,admin,null).statusCode());
        var input=edit("valid",media(),"projects");
        for(String field:List.of("categories","duration","areaM2","mediaIds","title")) {
            var invalid=new LinkedHashMap<>(input);invalid.put(field,switch(field){case "categories"->List.of("invented");case "duration"->Map.of("min",1,"max",2);case "areaM2"->-1;case "mediaIds"->List.of("../../file");default->Map.of("fr","Not supported");});
            assertEquals(422,call("PUT",base,invalid,admin,etag(draft)).statusCode(),field);
        }
        var blog=edit("article",input.get("coverImageId").toString(),"blog");blog.put("description",Map.of("de","![Bild](/api/media/12345678-1234-1234-1234-123456789abc/large)","en","Article"));
        assertEquals(422,call("POST","/api/admin/blog",blog,admin,null).statusCode());
    }
    @Test void homepageSelectionIsOneVersionedDocumentWithDefinedOrder() throws Exception {
        String image=media();List<String> ids=new ArrayList<>();
        for(int i=0;i<3;i++){var created=call("POST","/api/admin/projects",edit("project-"+i,image,"projects"),admin,null);String id=node(created).get("id").asText();ids.add(id);assertEquals(200,call("POST","/api/admin/projects/"+id+"/publish",null,admin,etag(created)).statusCode());}
        var initial=call("GET","/api/admin/homepage-projects",null,admin,null);assertEquals("\"0\"",etag(initial));Collections.reverse(ids);
        var changed=call("PUT","/api/admin/homepage-projects",Map.of("projectIds",ids),admin,etag(initial));assertEquals(200,changed.statusCode());
        var featured=node(call("GET","/api/projects/featured?lang=de",null,null,null)).get("items");assertEquals(ids.get(0),featured.get(0).get("id").asText());
        assertEquals(409,call("PUT","/api/admin/homepage-projects",Map.of("projectIds",List.of()),admin,etag(initial)).statusCode());
        assertEquals(422,call("PUT","/api/admin/homepage-projects",Map.of("projectIds",List.of(ids.get(0),ids.get(0))),admin,etag(changed)).statusCode());
        assertEquals(422,call("PUT","/api/admin/homepage-projects",Map.of("projectIds",List.of("one","two","three","four")),admin,etag(changed)).statusCode());
    }
    @Test void uploadsAreBoundedSniffedPrivateAndMetadataUpdatesAreVersioned() throws Exception {
        String image=media();var metadata=call("GET","/api/admin/media/"+image,null,admin,null);
        assertEquals(404,call("GET","/api/media/"+image+"/small?preview=true",null,null,null).statusCode());
        assertEquals(422,upload("<svg onload='evil()'/>".getBytes(),"photo.jpg",admin).statusCode());
        assertEquals(413,upload(new byte[1100000],"large.png",admin).statusCode());
        var duplicate=upload(image(),"../../ignored-name.png",admin);assertEquals(201,duplicate.statusCode());assertEquals(image,node(duplicate).get("id").asText());assertEquals(1,data.mediaCount());
        var changed=call("PUT","/api/admin/media/"+image+"/alt",Map.of("alt",Map.of("de","Neuer Text","en","New text")),admin,etag(metadata));assertEquals(200,changed.statusCode());
        assertEquals(409,call("PUT","/api/admin/media/"+image+"/alt",Map.of("alt",Map.of()),admin,etag(metadata)).statusCode());
        assertEquals(404,call("GET","/api/admin/media/"+image+"/original",null,admin,null).statusCode());
        assertEquals(405,call("DELETE","/api/admin/media/"+image,null,admin,null).statusCode());assertTrue(Files.exists(files.publicFile(image,"large")));
    }
}
