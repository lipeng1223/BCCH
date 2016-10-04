package com.bc.util.cache;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class StateCacheElement implements Serializable {

    private String session;
    private HashMap<String, String> elements;
    
    public StateCacheElement(String session, String name, String value){
        this.session = session;
        elements = new HashMap<String, String>();
        elements.put(name, value);
    }
    
    public void addToElement(String name, String value){
        elements.put(name, value);
    }
    
    public void removeElement(String name){
        elements.remove(name);
    }
    
    public String toJson(){
        StringBuilder sb = new StringBuilder();
        boolean comma = false;
        for (String key : elements.keySet()){
            if (comma) sb.append(", ");
            else comma = true;
            sb.append("{\"name\":\"");
            sb.append(key);
            sb.append("\", \"value\":\"");
            sb.append(elements.get(key));
            sb.append("\"}");
        }
        return sb.toString();
    }
    
    public String getSession(){
        return session;
    }
    
}
