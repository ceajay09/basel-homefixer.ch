package com.example.myproject.admin;

import com.example.myproject.content.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.*;

/** Independent snapshots and atomic compare-and-set semantics, with no external database connection. */
class MemoryAdminStore extends AdminContentStore {
    final ObjectMapper json;
    final Map<String,ContentFields> contents=new HashMap<>();final Map<String,MediaAsset> assets=new HashMap<>();
    HomepageSelection selected;
    MemoryAdminStore(ObjectMapper json){super(null);this.json=json;}
    synchronized void clear(){contents.clear();assets.clear();selected=null;}
    String key(Class<?> type,String id){return type.getSimpleName()+":"+id;}
    <T> T copy(T value,Class<T> type){return value==null?null:json.convertValue(value,type);}
    @Override public synchronized <T extends ContentFields> T get(Class<T> type,String id){return copy(type.cast(contents.get(key(type,id))),type);}
    @Override public synchronized MediaAsset media(String id){return copy(assets.get(id),MediaAsset.class);}
    @Override public synchronized <T extends ContentFields> List<T> list(Class<T> type,String status,int page,int size){return contents.values().stream().filter(type::isInstance).map(type::cast)
        .filter(c->status==null||("legacy".equals(status)?c.status==null:status.equals(c.status))).sorted(Comparator.comparing(c->c.id)).skip((long)page*size).limit(size).map(c->copy(c,type)).toList();}
    @Override public synchronized long count(Class<? extends ContentFields> type,String status){return list(type,status,0,Integer.MAX_VALUE).size();}
    @Override public synchronized boolean slugTaken(Class<? extends ContentFields> type,String slug,String exceptId){return contents.values().stream().anyMatch(c->type.isInstance(c)&&Objects.equals(c.slug,slug)&&!c.id.equals(exceptId));}
    @Override public void ensureSlugIndex(Class<? extends ContentFields> type){}
    @Override public synchronized <T extends ContentFields> T insert(T content){
        if(contents.containsKey(key(content.getClass(),content.id))||slugTaken(content.getClass(),content.slug,content.id))throw new org.springframework.dao.DuplicateKeyException("Test unique conflict");
        contents.put(key(content.getClass(),content.id),json.convertValue(content,content.getClass()));return content;
    }
    @Override public synchronized boolean replace(ContentFields content,long version){
        var current=contents.get(key(content.getClass(),content.id));if(current==null||version(current.version)!=version)return false;
        if(slugTaken(content.getClass(),content.slug,content.id))throw new org.springframework.dao.DuplicateKeyException("Test unique conflict");
        contents.put(key(content.getClass(),content.id),json.convertValue(content,content.getClass()));return true;
    }
    @Override public synchronized HomepageSelection selection(){
        if(selected!=null)return copy(selected,HomepageSelection.class);
        var fallback=new HomepageSelection();fallback.version=0L;fallback.projectIds=contents.values().stream().filter(ProjectContent.class::isInstance).map(ProjectContent.class::cast)
            .filter(p->p.homeOrder!=null&&p.homeOrder>=0).sorted(Comparator.comparingInt(p->p.homeOrder)).limit(3).map(p->p.id).toList();return fallback;
    }
    @Override public synchronized boolean replaceSelection(HomepageSelection next,long version){
        if((selected==null?0:version(selected.version))!=version)return false;selected=copy(next,HomepageSelection.class);return true;
    }
    @Override public synchronized MediaAsset insertMedia(MediaAsset asset){
        if(assets.containsKey(asset.id))throw new org.springframework.dao.DuplicateKeyException("Test media conflict");assets.put(asset.id,copy(asset,MediaAsset.class));return asset;
    }
    @Override public synchronized List<MediaAsset> mediaList(int page,int size){return assets.values().stream().sorted(Comparator.comparing(m->m.id)).skip((long)page*size).limit(size).map(m->copy(m,MediaAsset.class)).toList();}
    @Override public synchronized long mediaCount(){return assets.size();}
    @Override public synchronized boolean replaceAlt(String id,Map<String,String> alt,long version){var m=assets.get(id);if(m==null||version(m.version)!=version)return false;m.alt=alt;m.version=version+1;return true;}
}
