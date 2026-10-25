package com.example.myproject.content;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.MemoryCacheImageInputStream;

/** Private immutable JPEG variants. Import and authenticated upload share this exact processing path. */
public class MediaStorage {
    public static final int MAX_BYTES=15*1024*1024;
    private final Path root;
    public MediaStorage(Path root){this.root=root.toAbsolutePath().normalize();}
    public record Prepared(MediaAsset asset,Map<String,byte[]> bytes){}
    public static boolean validId(String id){return id!=null&&id.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");}
    static String hash(byte[] bytes){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
    public Prepared prepare(Path sourceRoot,String filename,Map<String,String> alt) throws IOException {
        if(filename==null||filename.isBlank()||filename.contains("/")||filename.contains("\\")||!Path.of(filename).getFileName().toString().equals(filename))throw new IOException("Invalid source filename");
        Path source=sourceRoot.toRealPath().resolve(filename);
        if(Files.isSymbolicLink(source)||!source.toRealPath().startsWith(sourceRoot.toRealPath())||!Files.isRegularFile(source))throw new IOException("Invalid source path");
        if(Files.size(source)>MAX_BYTES)throw new IOException("Image exceeds 15 MiB");
        try(var input=Files.newInputStream(source)){return prepare(input.readNBytes(MAX_BYTES+1),alt);}
    }
    public Prepared prepare(byte[] original,Map<String,String> alt) throws IOException {
        if(original==null||original.length==0||original.length>MAX_BYTES)throw new IOException("Invalid image size");
        String digest=hash(original);var asset=new MediaAsset();
        // Versioned algorithm: v1 files/IDs remain readable and are never silently overwritten.
        asset.id=UUID.nameUUIDFromBytes(("image-v2:"+digest).getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
        asset.sourceSha256=digest;asset.alt=new HashMap<>(alt);
        Map<String,byte[]> output=new HashMap<>();
        try(var input=new MemoryCacheImageInputStream(new ByteArrayInputStream(original))) {
            var readers=ImageIO.getImageReaders(input);if(!readers.hasNext())throw new IOException("Not a supported raster image");var reader=readers.next();
            try {
                String format=reader.getFormatName().toLowerCase(Locale.ROOT);if(!Set.of("jpeg","jpg","png").contains(format))throw new IOException("Only JPEG and PNG are accepted");
                reader.setInput(input,true,true);int width=reader.getWidth(0),height=reader.getHeight(0);
                if(width<1||height<1||(long)width*height>25000000L)throw new IOException("Image exceeds 25 million pixels");
                int orientation=ExifOrientation.read(original);boolean transpose=orientation>=5;
                int orientedWidth=transpose?height:width, orientedHeight=transpose?width:height;
                BufferedImage decoded=reader.read(0);
                try {
                    for(var variant:Map.of("small",480,"large",1280).entrySet()) {
                        double scale=Math.min(1,(double)variant.getValue()/Math.max(orientedWidth,orientedHeight));
                        int w=Math.max(1,(int)Math.round(orientedWidth*scale)),h=Math.max(1,(int)Math.round(orientedHeight*scale));
                        BufferedImage resized=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);Graphics2D g=resized.createGraphics();
                        try {
                            g.setColor(Color.WHITE);g.fillRect(0,0,w,h);g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                            g.scale((double)w/orientedWidth,(double)h/orientedHeight);g.transform(ExifOrientation.transform(orientation,width,height));g.drawImage(decoded,0,0,null);
                        } finally {g.dispose();}
                        var bytes=new ByteArrayOutputStream();try{if(!ImageIO.write(resized,"jpeg",bytes))throw new IOException("JPEG encoder unavailable");}finally{resized.flush();}
                        byte[] encoded=bytes.toByteArray();output.put(variant.getKey(),encoded);asset.variants.put(variant.getKey(),new MediaAsset.Variant(w,h,hash(encoded)));
                    }
                } finally {decoded.flush();}
            } finally {reader.dispose();}
        } catch(RuntimeException invalid) {throw new IOException("Invalid raster image",invalid);}
        return new Prepared(asset,output);
    }
    public synchronized void put(Prepared prepared) throws IOException {
        String id=prepared.asset().id;if(!validId(id))throw new IOException("Invalid media id");Files.createDirectories(root);
        Path folder=root.resolve(id);if(Files.isSymbolicLink(folder))throw new IOException("Symlink not allowed");
        if(Files.exists(folder)){verifyExisting(prepared);return;}
        Path stage=Files.createTempDirectory(root,".staging-");
        try {
            for(var entry:prepared.bytes().entrySet())Files.write(stage.resolve(entry.getKey()+".jpg"),entry.getValue(),StandardOpenOption.CREATE_NEW);
            try {Files.move(stage,folder,StandardCopyOption.ATOMIC_MOVE);}
            catch(AtomicMoveNotSupportedException unsupported){Files.move(stage,folder);}
            catch(FileAlreadyExistsException concurrent){verifyExisting(prepared);}
        } finally {
            if(Files.exists(stage)){for(String name:Set.of("small.jpg","large.jpg"))Files.deleteIfExists(stage.resolve(name));Files.deleteIfExists(stage);}
        }
    }
    private void verifyExisting(Prepared prepared) throws IOException {
        for(var entry:prepared.bytes().entrySet())if(!hash(Files.readAllBytes(publicFile(prepared.asset().id,entry.getKey()))).equals(hash(entry.getValue())))throw new IOException("Immutable media differs");
    }
    public Path publicFile(String id,String variant) throws IOException {
        if(!validId(id)||!Set.of("small","large").contains(variant))throw new IOException("Invalid media reference");
        Path folder=root.resolve(id),file=folder.resolve(variant+".jpg");
        if(Files.isSymbolicLink(folder)||Files.isSymbolicLink(file)||!Files.isRegularFile(file)||!file.toRealPath().startsWith(root.toRealPath()))throw new IOException("Invalid media path");
        return file;
    }
    // No deletion API: both content types, including drafts, must be checked before any future removal.
}
