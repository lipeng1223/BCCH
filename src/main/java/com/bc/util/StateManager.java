package com.bc.util;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.bc.util.cache.StateCache;

public class StateManager {
    
    private static Logger logger = Logger.getLogger(StateManager.class);
    
    private StateManager(){}
    
    public static void removeState(Long id, String session){
        StateCache.remove(id, session);
    }

    public static void removeState(Long id, String session, String elemName){
        StateCache.remove(id, session, elemName);
    }
    
    public static String readState(Long id, String session){
        StringBuilder state = new StringBuilder("[");
        state.append(StateCache.get(id, session));
        state.append("]");
        //logger.info("read state for id: "+id+" session: "+session);
        //logger.info("state: "+state.toString());
        return state.toString();
    }
    
    public static void saveState(Long id, String session, String state){
        try {
            //logger.info("saving state for id: "+id+" session: "+session);
            JSONObject jsonObject = new JSONObject(state);
            //logger.info("jsonObject name: "+jsonObject.getString("name"));
            //logger.info("jsonObject value: "+jsonObject.getString("value"));
            StateCache.put(id, session, jsonObject.getString("name"), jsonObject.getString("value"));
        } catch (JSONException jse){
            logger.error("JSONException in saveState: "+jse.getMessage());
            logger.error("id: "+id+" session: "+session+" State: ");
            logger.error("-------------------------------------------------------------");
            logger.error(state);
            logger.error("-------------------------------------------------------------");
        }
    }
}
