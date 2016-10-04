package com.bc.util.cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public class LoginSessionCache {

    private static Map<String, HttpSession> cache;
    
    static {
        cache = Collections.synchronizedMap(new HashMap<String, HttpSession>());
    }
    
    private LoginSessionCache(){}
    
    public synchronized static void add(HttpSession session){
        if (cache.containsKey(session.getId())){
            return;
        }
        cache.put(session.getId(), session);
    }
    
    public synchronized static HttpSession getByUsername(String username){
        for (String id : cache.keySet()){
            HttpSession hs = cache.get(id);
            if (username.equals(hs.getAttribute("username"))){
                return hs;
            }
        }
        return null;
    }
    
    public static HttpSession getById(String id){
        return cache.get(id);
    }
    
    public static HttpSession removeById(String id){
        return cache.remove(id);
    }
    
    public synchronized static void removeByUsername(String username, String currentId){
        List<String> keyset = new ArrayList<String>(cache.keySet());
        for (String id : keyset){
            HttpSession hs = cache.get(id);
            if (username.equals(hs.getAttribute("username")) && !id.equals(currentId)){
                cache.remove(id);
                hs.invalidate(); // causes a logout for this username
            }
        }
    }
    
    public static Integer getSize(){
        return cache.size();
    }
    
    public static String getStatus(){
        StringBuilder sb = new StringBuilder();
        sb.append("HttpSession cache size: ");
        sb.append(getSize());
        sb.append("\n\nCurrent logged in users:\n\n");
        sb.append("-------------------------------------------------------------------------------------------\n");
        sb.append("last access time - time diff - username - session id\n");
        sb.append("null username usually means the user logged off\nand the page went to the login screen\nor is at the alreadyLoggedIn jsp.\n");
        sb.append("-------------------------------------------------------------------------------------------\n\n");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy HH:mm:ss a");
        Date now = Calendar.getInstance().getTime();
        for (String id : cache.keySet()){
            HttpSession hs = cache.get(id);
            sb.append(sdf.format(new Date(hs.getLastAccessedTime())));
            sb.append(" - ");
            sb.append((now.getTime() - hs.getLastAccessedTime())/(1000 * 60));
            sb.append(" minutes - ");
            sb.append(hs.getAttribute("username"));
            sb.append(" - ");
            sb.append(hs.getId());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public static Map<String, HttpSession> getCache(){
        return cache;
    }
}
