package com.example.myproject.content;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class MediaStorageTest {
    @TempDir Path temp;
    @Test void sanitisesResizesAndReusesImmutableVariants() throws Exception {
        Path input=Files.createDirectory(temp.resolve("input"));var image=new BufferedImage(1000,800,BufferedImage.TYPE_INT_ARGB);ImageIO.write(image,"png",input.resolve("photo.png").toFile());
        Path root=temp.resolve("private");var storage=new MediaStorage(root);var prepared=storage.prepare(input,"photo.png",Map.of("de","Bild","en","Photo"));
        assertFalse(Files.exists(root));assertEquals(480,prepared.asset().variants.get("small").width());assertEquals(1000,prepared.asset().variants.get("large").width());
        storage.put(prepared);Path file=storage.publicFile(prepared.asset().id,"small");byte[] before=Files.readAllBytes(file);try(var stream=ImageIO.createImageInputStream(file.toFile())){var reader=ImageIO.getImageReaders(stream).next();try{assertEquals("JPEG",reader.getFormatName().toUpperCase());}finally{reader.dispose();}}
        storage.put(prepared);assertArrayEquals(before,Files.readAllBytes(file));assertEquals(prepared.asset().id,storage.prepare(input,"photo.png",Map.of()).asset().id);
        assertThrows(java.io.IOException.class,()->storage.publicFile(prepared.asset().id,"original"));
    }
    @Test void rejectsPathsActiveFilesAndOversizedInputs() throws Exception {
        Path input=Files.createDirectory(temp.resolve("input"));Files.writeString(input.resolve("fake.jpg"),"<svg onload='alert(1)'/>");
        var storage=new MediaStorage(temp.resolve("private"));
        assertThrows(java.io.IOException.class,()->storage.prepare(input,"fake.jpg",Map.of()));
        assertThrows(java.io.IOException.class,()->storage.prepare(input,"../secret.jpg",Map.of()));
        assertThrows(java.io.IOException.class,()->storage.prepare(input,"..\\secret.jpg",Map.of()));
        assertThrows(java.io.IOException.class,()->storage.publicFile("../../secret","small"));
        try(var file=new java.io.RandomAccessFile(input.resolve("large.jpg").toFile(),"rw")){file.setLength(16L*1024*1024);}
        assertThrows(java.io.IOException.class,()->storage.prepare(input,"large.jpg",Map.of()));
    }
    @Test void refusesTamperedExistingFile() throws Exception {
        Path input=Files.createDirectory(temp.resolve("input"));ImageIO.write(new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB),"png",input.resolve("a.png").toFile());
        var storage=new MediaStorage(temp.resolve("private"));var p=storage.prepare(input,"a.png",Map.of());storage.put(p);Files.writeString(storage.publicFile(p.asset().id,"small"),"tampered");
        assertThrows(java.io.IOException.class,()->storage.put(p));
    }
    @Test void pixelLimitIsCheckedBeforeDecode() throws Exception {
        Path input=Files.createDirectory(temp.resolve("input"));var bytes=new java.io.ByteArrayOutputStream();var data=new java.io.DataOutputStream(bytes);
        data.writeLong(0x89504e470d0a1a0aL);var header=new java.io.ByteArrayOutputStream();var h=new java.io.DataOutputStream(header);h.writeInt(100000);h.writeInt(100000);h.write(new byte[]{8,2,0,0,0});
        data.writeInt(13);byte[] type="IHDR".getBytes(java.nio.charset.StandardCharsets.US_ASCII);data.write(type);data.write(header.toByteArray());var crc=new java.util.zip.CRC32();crc.update(type);crc.update(header.toByteArray());data.writeInt((int)crc.getValue());
        Files.write(input.resolve("bomb.png"),bytes.toByteArray());assertThrows(java.io.IOException.class,()->new MediaStorage(temp.resolve("private")).prepare(input,"bomb.png",Map.of()));
    }
}
