package com.bc.util.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.bc.orm.Vendor;

public class VendorCache {

    private static Cache cache;

    private static Logger logger = Logger.getLogger(VendorCache.class);
    
    private VendorCache(){}
    
    static {
        cache = CacheManager.getInstance().getCache("vendorCache");
    }
    
    public static void removeAll(){
        try {
            cache.removeAll();
            logger.info("Cleared the vendor cache");
        } catch (Exception e){
            logger.error("Could not remove everything from the vendor cache.");
        }
    }

    public static void put(Vendor vendor){
        if (!cache.isKeyInCache(vendor.getId())){
            cache.put(new Element(vendor.getId(), vendor));
        }
    }

    public static Boolean inCache(Long vendorId){
        return cache.isKeyInCache(vendorId);
    }
    
    public static void remove(Long vendorId){
        try {
            if (cache.isKeyInCache(vendorId)){
                cache.remove(vendorId);
            }
            logger.info("Cleared the cache for vendorId: "+vendorId);
        } catch (Exception e){
            logger.error("Cache remove for vendorId: "+vendorId, e);
        }
    }
    
    public static List<Vendor> getVendors(){
        List<Vendor> vendors = new ArrayList<Vendor>(cache.getSize());
        for (Long id : (List<Long>)cache.getKeys()){
            vendors.add((Vendor)cache.get(id).getValue());
        }
        Collections.sort(vendors);
        return vendors;
    }
    
    public static String getStatus(){
        StringBuilder sb = new StringBuilder();
        sb.append("Vendor Cache size: ");
        sb.append(cache.getSize());
        sb.append("\n");
        sb.append("Vendor Cache disk size: ");
        sb.append(cache.getDiskStoreSize());
        sb.append("\nVendor Cache status: ");
        sb.append(cache.getStatus().toString());
        sb.append("\n\nCache Keys:\n");
        sb.append("--------------------------------------------------------------------------------------------------------------------------\n");
        for (Object key : cache.getKeys()){
            sb.append("Vendor ID: ");
            sb.append(key);
        }
        return sb.toString();
    }
    
}
