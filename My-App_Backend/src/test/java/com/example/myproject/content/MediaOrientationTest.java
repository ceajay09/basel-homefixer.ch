package com.example.myproject.content;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class MediaOrientationTest {
    @TempDir Path temp;
    byte[] jpeg() throws Exception {
        var image=new BufferedImage(80,40,BufferedImage.TYPE_INT_RGB);var graphics=image.createGraphics();
        try{graphics.setColor(Color.RED);graphics.fillRect(0,0,40,20);graphics.setColor(Color.GREEN);graphics.fillRect(40,0,40,20);graphics.setColor(Color.BLUE);graphics.fillRect(0,20,40,20);graphics.setColor(Color.YELLOW);graphics.fillRect(40,20,40,20);}finally{graphics.dispose();}
        var bytes=new ByteArrayOutputStream();ImageIO.write(image,"jpeg",bytes);return bytes.toByteArray();
    }
    byte[] tiff(int orientation) {
        return ByteBuffer.allocate(26).order(ByteOrder.LITTLE_ENDIAN).put((byte)'I').put((byte)'I').putShort((short)42).putInt(8)
            .putShort((short)1).putShort((short)274).putShort((short)3).putInt(1).putShort((short)orientation).putShort((short)0).putInt(0).array();
    }
    byte[] oriented(byte[] jpeg,int orientation)throws Exception {
        var out=new ByteArrayOutputStream();out.write(jpeg,0,2);var data=new DataOutputStream(out);data.writeShort(0xffe1);data.writeShort(34);data.write(new byte[]{'E','x','i','f',0,0});data.write(tiff(orientation));out.write(jpeg,2,jpeg.length-2);return out.toByteArray();
    }
    @Test void allEightExifOrientationsAreAppliedIncludingMirrors() throws Exception {
        Color[] corners={Color.RED,Color.GREEN,Color.YELLOW,Color.BLUE,Color.RED,Color.BLUE,Color.YELLOW,Color.GREEN};
        var storage=new MediaStorage(temp);
        for(int orientation=1;orientation<=8;orientation++) {
            byte[] original=oriented(jpeg(),orientation);assertEquals(orientation,ExifOrientation.read(original));
            var prepared=storage.prepare(original,Map.of());var decoded=ImageIO.read(new ByteArrayInputStream(prepared.bytes().get("large")));
            assertEquals(orientation>=5?40:80,decoded.getWidth());assertEquals(orientation>=5?80:40,decoded.getHeight());
            var actual=new Color(decoded.getRGB(decoded.getWidth()/4,decoded.getHeight()/4));var expected=corners[orientation-1];
            int delta=Math.abs(actual.getRed()-expected.getRed())+Math.abs(actual.getGreen()-expected.getGreen())+Math.abs(actual.getBlue()-expected.getBlue());
            assertTrue(delta<100,"Incorrect corner for EXIF "+orientation);assertEquals(1,ExifOrientation.read(prepared.bytes().get("large")));
        }
    }
    @Test void dimensionsAreBoundedOnBothAxesAndLegacyIdsRemainReadable()throws Exception {
        var storage=new MediaStorage(temp);var input=new BufferedImage(10,2000,BufferedImage.TYPE_INT_RGB);var bytes=new ByteArrayOutputStream();ImageIO.write(input,"png",bytes);
        var result=storage.prepare(bytes.toByteArray(),Map.of());assertTrue(result.asset().variants.get("small").height()<=480);assertTrue(result.asset().variants.get("large").height()<=1280);
        String legacy=UUID.nameUUIDFromBytes(("image-v1:"+MediaStorage.hash(bytes.toByteArray())).getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
        assertNotEquals(legacy,result.asset().id);Files.createDirectory(temp.resolve(legacy));Files.write(temp.resolve(legacy).resolve("small.jpg"),result.bytes().get("small"));
        assertTrue(Files.exists(storage.publicFile(legacy,"small")));
    }
    @Test void pngExifIsUnderstoodAndMalformedOffsetsAreSafe()throws Exception {
        var image=new BufferedImage(30,10,BufferedImage.TYPE_INT_RGB);var bytes=new ByteArrayOutputStream();ImageIO.write(image,"png",bytes);byte[] png=bytes.toByteArray();
        var output=new ByteArrayOutputStream();output.write(png,0,33);var data=new DataOutputStream(output);byte[] type={'e','X','I','f'},exif=tiff(8);data.writeInt(exif.length);data.write(type);data.write(exif);
        var crc=new java.util.zip.CRC32();crc.update(type);crc.update(exif);data.writeInt((int)crc.getValue());output.write(png,33,png.length-33);
        assertEquals(8,ExifOrientation.read(output.toByteArray()));var result=new MediaStorage(temp).prepare(output.toByteArray(),Map.of());assertEquals(10,result.asset().variants.get("large").width());
        assertEquals(1,ExifOrientation.read(new byte[]{(byte)255,(byte)216,(byte)255,(byte)225,0,0}));
    }
}
