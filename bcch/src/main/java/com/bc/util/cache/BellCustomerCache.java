package com.bc.util.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.bc.orm.BellCustomer;

public class BellCustomerCache {

    private static Cache cache;

    private static Logger logger = Logger.getLogger(BellCustomerCache.class);
    
    private BellCustomerCache(){}
    
    static {
        cache = CacheManager.getInstance().getCache("bellCustomerCache");
    }
    
    public static void removeAll(){
        try {
            cache.removeAll();
            logger.info("Cleared the customer cache");
        } catch (Exception e){
            logger.error("Could not remove everything from the customer cache.");
        }
    }

    public static void put(BellCustomer customer){
        if (!cache.isKeyInCache(customer.getId())){
            cache.put(new Element(customer.getId(), customer));
        }
    }

    public static Boolean inCache(Long customerId){
        return cache.isKeyInCache(customerId);
    }
    
    public static void remove(Long customerId){
        try {
            if (cache.isKeyInCache(customerId)){
                cache.remove(customerId);
            }
            logger.info("Cleared the cache for customerId: "+customerId);
        } catch (Exception e){
            logger.error("Cache remove for customerId: "+customerId, e);
        }
    }
    
    public static List<BellCustomer> getCustomers(){
        List<BellCustomer> customers = new ArrayList<BellCustomer>(cache.getSize());
        for (Long id : (List<Long>)cache.getKeys()){
            customers.add((BellCustomer)cache.get(id).getValue());
        }
        Collections.sort(customers);
        return customers;
    }
    
    public static String getStatus(){
        StringBuilder sb = new StringBuilder();
        sb.append("Customer Cache size: ");
        sb.append(cache.getSize());
        sb.append("\n");
        sb.append("Customer Cache disk size: ");
        sb.append(cache.getDiskStoreSize());
        sb.append("\nCustomer Cache status: ");
        sb.append(cache.getStatus().toString());
        sb.append("\n\nCache Keys:\n");
        sb.append("--------------------------------------------------------------------------------------------------------------------------\n");
        for (Object key : cache.getKeys()){
            sb.append("Customer ID: ");
            sb.append(key);
        }
        return sb.toString();
    }
    
}
