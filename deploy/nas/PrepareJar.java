import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;

/** Makes a separate release candidate; never edits the normal Maven artefact. */
public class PrepareJar {
  public static void main(String[] args) throws Exception {
    if(args.length!=2)throw new IllegalArgumentException("Source and NEW destination required");
    Path source=Path.of(args[0]).toRealPath(),target=Path.of(args[1]).toAbsolutePath().normalize();
    if(source.equals(target)||Files.exists(target))throw new IllegalArgumentException("Destination must be new");
    Set<String> allowed=Set.of("contact.properties","logback.xml","META-INF/additional-spring-configuration-metadata.json");
    int excluded=0;boolean loader=false;
    try(ZipFile input=new ZipFile(source.toFile());var output=new ZipOutputStream(Files.newOutputStream(target,StandardOpenOption.CREATE_NEW))){
      var entries=input.entries();while(entries.hasMoreElements()){
        ZipEntry entry=entries.nextElement();String name=entry.getName();String lower=name.toLowerCase(Locale.ROOT);
        boolean skip=lower.contains("naspreview")||lower.contains("nas-preview")||lower.contains("environment-variables")||lower.endsWith(".env")||lower.contains("/application-dev.");
        if(name.startsWith("BOOT-INF/classes/")&&!entry.isDirectory()){
          String resource=name.substring("BOOT-INF/classes/".length());
          skip|=!resource.endsWith(".class")&&!allowed.contains(resource);
        }
        if(skip){excluded++;continue;}
        ZipEntry copy=new ZipEntry(name);copy.setTime(entry.getTime());copy.setMethod(entry.getMethod());
        if(entry.getMethod()==ZipEntry.STORED){copy.setSize(entry.getSize());copy.setCompressedSize(entry.getSize());copy.setCrc(entry.getCrc());}
        output.putNextEntry(copy);if(!entry.isDirectory())try(var bytes=input.getInputStream(entry)){bytes.transferTo(output);}output.closeEntry();
        loader|=name.equals("org/springframework/boot/loader/launch/PropertiesLauncher.class");
      }
      // Existing @PropertySource requires this resource; never copy its old contents.
      output.putNextEntry(new ZipEntry("BOOT-INF/classes/application.properties"));
      output.write("# External production configuration is mandatory; no embedded defaults.\\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
      output.closeEntry();
    }
    if(!loader)throw new IllegalStateException("PropertiesLauncher missing; operator tools cannot run");
    System.out.println("Prepared separate release JAR; excluded "+excluded+" non-production entries. Original preserved.");
  }
}

