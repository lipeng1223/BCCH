package com.bc.util.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

public class StateCache {

    private static Cache cache;

    private static Logger logger = Logger.getLogger(StateCache.class);
    
    private StateCache(){}
    
    static {
        cache = CacheManager.getInstance().getCache("guiStateCache");
    }
    
    public static void shutdown(){
        try {
            CacheManager.getInstance().shutdown();
        } catch (Throwable t){
            logger.error("Could not shutdown the cachemanager.", t);
        }
    }
    
    public static void removeAll(){
        try {
            cache.removeAll();
            logger.info("Cleared the states cache");
        } catch (Exception e){
            logger.error("Could not remove everything from the states cache.");
        }
    }

    public static void put(Long userId, String session, String name, String value){
        if (userId == null || session == null || name == null || value == null) return;
        String cacheKey = userId.toString()+"-"+session;
        if (!cache.isKeyInCache(cacheKey)){
            Element elem = new Element(cacheKey, new StateCacheElement(session, name, value));
            cache.put(elem);
        } else {
            Element elem = cache.get(cacheKey);
            StateCacheElement sce = (StateCacheElement)elem.getValue();
            sce.addToElement(name, value);
            cache.put(elem);
        }
        // flush the cache to disk
        cache.flush();
    }

    /**
     * Returns the json for this userId / session
     */
    public static String get(Long userId, String session){
        if (userId == null) return "";
        try {
            String cacheKey = userId.toString()+"-"+session;
            if (cache.isKeyInCache(cacheKey)){
                Element elem = cache.get(cacheKey);
                return  ((StateCacheElement)elem.getValue()).toJson();
            }
        } catch (Exception e){
            logger.error("Cache get for userId: "+userId+" session: "+session+" failed.", e);
        }
        return ""; // blank
    }

    public static void remove(Long userId, String session){
        if (userId == null) return;
        try {
            String cacheKey = userId.toString()+"-"+session;
            if (cache.isKeyInCache(cacheKey)){
                cache.remove(cacheKey);
            }
            logger.info("Cleared the cache for userId: "+userId+" session: "+session);
        } catch (Exception e){
            logger.error("Cache remove for userId: "+userId+" session: "+session+" failed.", e);
        }
    }
    
    public static void remove(Long userId, String session, String elemName){
        if (userId == null) return;
        try {
            String cacheKey = userId.toString()+"-"+session;
            if (cache.isKeyInCache(cacheKey)){
                Element elem = cache.get(cacheKey);
                ((StateCacheElement)elem.getValue()).removeElement(elemName);
            }
            logger.info("Cleared the cache for userId: "+userId+" session: "+session+" elemName: "+elemName);
        } catch (Exception e){
            logger.error("Cache remove for userId: "+userId+" session: "+session+" elemName: "+elemName+" failed.", e);
        }
    }
    
    public static String getStatus(){
        StringBuilder sb = new StringBuilder();
        sb.append("State Cache size: ");
        sb.append(cache.getSize());
        sb.append("\n");
        sb.append("State Cache disk size: ");
        sb.append(cache.getDiskStoreSize());
        sb.append("\nState Cache status: ");
        sb.append(cache.getStatus().toString());
        sb.append("\n\nCache Keys:\n");
        sb.append("--------------------------------------------------------------------------------------------------------------------------\n");
        for (Object key : cache.getKeys()){
            sb.append(key);
            sb.append("\n");
        }
        return sb.toString();
    }
}
