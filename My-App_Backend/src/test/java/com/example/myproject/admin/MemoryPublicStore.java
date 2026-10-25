package com.example.myproject.admin;

import com.example.myproject.content.*;
import com.example.myproject.model.Blogpost;
import java.time.Instant;
import java.util.*;

class MemoryPublicStore extends ContentStore {
    private final MemoryAdminStore data;
    MemoryPublicStore(MemoryAdminStore data){super(null);this.data=data;}
    private <T extends ContentFields> List<T> visible(Class<T> type,String category,boolean featured){
        var all=new ArrayList<>(data.list(type,null,0,Integer.MAX_VALUE).stream().filter(c->c.published(Instant.now())).filter(c->category==null||c.categories.contains(category)).toList());
        var ids=data.selection().projectIds;
        if(featured)all.removeIf(c->!ids.contains(c.id));
        all.sort(featured?Comparator.comparingInt(c->ids.indexOf(c.id)):Comparator.comparing((T c)->c.publishedAt).reversed().thenComparing(c->c.id));return all;
    }
    @Override public <T extends ContentFields> List<T> list(Class<T> type,String category,int page,int size,boolean featured){return visible(type,category,featured).stream().skip((long)page*size).limit(size).toList();}
    @Override public <T extends ContentFields> long count(Class<T> type,String category,boolean featured){return visible(type,category,featured).size();}
    @Override public <T extends ContentFields> T byId(Class<T> type,String id){return data.get(type,id);}
    @Override public <T extends ContentFields> T bySlug(Class<T> type,String slug){return visible(type,null,false).stream().filter(c->slug.equals(c.slug)).findFirst().orElse(null);}
    @Override public MediaAsset media(String id){return data.media(id);}
    @Override public boolean publicReference(String id){return java.util.stream.Stream.concat(visible(ProjectContent.class,null,false).stream(),visible(Blogpost.class,null,false).stream()).anyMatch(c->id.equals(c.coverImageId)||c.mediaIds.contains(id));}
    @Override public boolean anyReference(String id){return java.util.stream.Stream.concat(data.list(ProjectContent.class,null,0,Integer.MAX_VALUE).stream(),data.list(Blogpost.class,null,0,Integer.MAX_VALUE).stream()).anyMatch(c->id.equals(c.coverImageId)||c.mediaIds.contains(id));}
}
