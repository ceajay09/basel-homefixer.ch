import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import java.util.jar.*;
public class ValidateRelease {
 public static void main(String[] args)throws Exception{
  if(args.length!=1)throw new IllegalArgumentException("Candidate JAR required");int count=0;
  try(ZipFile jar=new ZipFile(args[0])){
   for(var it=jar.entries();it.hasMoreElements();){var e=it.nextElement();String n=e.getName().toLowerCase(Locale.ROOT);
    if(n.contains("application-dev")||n.contains("environment-variables")||n.endsWith(".env")||n.contains("naspreview")||n.contains("mywebapp.blogpost.json"))throw new IllegalStateException("Forbidden release resource");
    if(n.startsWith("boot-inf/lib/")&&!e.isDirectory()&&e.getMethod()!=ZipEntry.STORED)throw new IllegalStateException("Nested library must remain STORED");
    if(!e.isDirectory()){var crc=new CRC32();try(var input=jar.getInputStream(e)){byte[] b=new byte[65536];int c;while((c=input.read(b))!=-1)crc.update(b,0,c);}if(crc.getValue()!=e.getCrc())throw new IllegalStateException("CRC mismatch");}count++;
   }
   var emptyDefaults=jar.getEntry("BOOT-INF/classes/application.properties");
   if(emptyDefaults==null)throw new IllegalStateException("Missing legacy PropertySource stub");
   var properties=new java.util.Properties();try(var input=jar.getInputStream(emptyDefaults)){properties.load(input);}
   if(!properties.isEmpty())throw new IllegalStateException("Embedded production defaults must remain empty");
   var manifest=new Manifest(jar.getInputStream(jar.getEntry("META-INF/MANIFEST.MF")));
   if(!"com.example.myproject.Application".equals(manifest.getMainAttributes().getValue("Start-Class")))throw new IllegalStateException("Unexpected app entrypoint");
   if(jar.getEntry("org/springframework/boot/loader/launch/PropertiesLauncher.class")==null)throw new IllegalStateException("Missing operator launcher");
  }
  System.out.println("PASS: release JAR integrity, boot entrypoint, stored nested libraries and forbidden-resource checks ("+count+" entries). No application or account tool started.");
 }
}

