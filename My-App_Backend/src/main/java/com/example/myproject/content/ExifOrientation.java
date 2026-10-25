package com.example.myproject.content;

import java.nio.*;
import java.awt.geom.AffineTransform;

/** Bounded EXIF orientation reader for JPEG APP1 / PNG eXIf. No GPS or other metadata is retained. */
final class ExifOrientation {
    private ExifOrientation() {}
    static int read(byte[] bytes) {
        try {
            if(bytes.length>=4 && (bytes[0]&255)==255 && (bytes[1]&255)==216) {
                int position=2;
                while(position+4<=bytes.length) {
                    if((bytes[position++]&255)!=255)return 1;
                    while(position<bytes.length && (bytes[position]&255)==255)position++;
                    if(position>=bytes.length)return 1;
                    int marker=bytes[position++]&255;
                    if(marker==218||marker==217)return 1;
                    if(marker==1||marker>=208&&marker<=215)continue;
                    int length=((bytes[position]&255)<<8)|(bytes[position+1]&255);
                    if(length<2 || (long)position+length>bytes.length)return 1;
                    int start=position+2;
                    if(marker==225 && length>=8 && bytes[start]=='E' && bytes[start+1]=='x' && bytes[start+2]=='i' && bytes[start+3]=='f' && bytes[start+4]==0 && bytes[start+5]==0)
                        return tiff(bytes,start+6,position+length);
                    position+=length;
                }
            } else if(bytes.length>=8 && ByteBuffer.wrap(bytes).getLong()==0x89504e470d0a1a0aL) {
                int position=8;
                while(position+12<=bytes.length) {
                    long length=Integer.toUnsignedLong(ByteBuffer.wrap(bytes,position,4).getInt());
                    if(length>bytes.length-position-12)return 1;
                    if(bytes[position+4]=='e'&&bytes[position+5]=='X'&&bytes[position+6]=='I'&&bytes[position+7]=='f')return tiff(bytes,position+8,position+8+(int)length);
                    position+=(int)length+12;
                }
            }
        } catch(IndexOutOfBoundsException|IllegalArgumentException malformed) { return 1; }
        return 1;
    }
    private static int tiff(byte[] bytes,int start,int end) {
        if(end-start<8)return 1;
        ByteOrder order=bytes[start]=='I'&&bytes[start+1]=='I'?ByteOrder.LITTLE_ENDIAN:bytes[start]=='M'&&bytes[start+1]=='M'?ByteOrder.BIG_ENDIAN:null;
        if(order==null)return 1;
        ByteBuffer buffer=ByteBuffer.wrap(bytes).order(order);
        if(Short.toUnsignedInt(buffer.getShort(start+2))!=42)return 1;
        long offset=Integer.toUnsignedLong(buffer.getInt(start+4));
        if(offset<8 || offset>end-start-2)return 1;
        int directory=start+(int)offset, count=Short.toUnsignedInt(buffer.getShort(directory));
        if((long)count*12>end-directory-2)return 1;
        for(int i=0;i<count;i++) {
            int entry=directory+2+i*12;
            if(Short.toUnsignedInt(buffer.getShort(entry))==274 && Short.toUnsignedInt(buffer.getShort(entry+2))==3 && buffer.getInt(entry+4)==1) {
                int value=Short.toUnsignedInt(buffer.getShort(entry+8));return value>=1&&value<=8?value:1;
            }
        }
        return 1;
    }
    static AffineTransform transform(int value,int width,int height) {
        return switch(value) {
            case 2->new AffineTransform(-1,0,0,1,width,0);
            case 3->new AffineTransform(-1,0,0,-1,width,height);
            case 4->new AffineTransform(1,0,0,-1,0,height);
            case 5->new AffineTransform(0,1,1,0,0,0);
            case 6->new AffineTransform(0,1,-1,0,height,0);
            case 7->new AffineTransform(0,-1,-1,0,height,width);
            case 8->new AffineTransform(0,-1,1,0,0,width);
            default->new AffineTransform();
        };
    }
}
