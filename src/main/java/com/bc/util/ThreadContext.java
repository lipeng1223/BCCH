package com.bc.util;

import java.util.HashMap;

public class ThreadContext {

    private static ThreadLocal<HashMap<String, String>> threadLocal = new ThreadLocal<HashMap<String, String>>();

    private ThreadContext(){}
    
    public static HashMap<String, String> getContext(){
        return threadLocal.get();
    }
    
    public static String get(String key){
        return threadLocal.get().get(key);
    }
    
    public static void setContext(Long userId, String username, String userAction){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", userId.toString());
        map.put("username", username);
        map.put("userAction", userAction);
        threadLocal.set(map);
    }
}
