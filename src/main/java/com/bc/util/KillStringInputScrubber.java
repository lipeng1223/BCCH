package com.bc.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class KillStringInputScrubber {

    private static final Logger logger = Logger.getLogger(KillStringInputScrubber.class);

    //public static String DEFAULT_INPUT_KILL_STRING = "'|\"|;|<|>|&#60|&#62|%3C|%3E|%3c|%3e";
    public static final String DEFAULT_INPUT_KILL_STRING = ";|<|>|&#60|&#62|%3C|%3E|%3c|%3e";
    
    private static String[] killStrings = DEFAULT_INPUT_KILL_STRING.split("\\|");

    private KillStringInputScrubber() {}
    
    public static String scrub(String sourceString) {
        if (sourceString == null){ 
            return null; 
        }

        String sourceCopy = sourceString;
        if (killStrings != null) {
            for (int i = 0; i < killStrings.length; i++) {
                sourceCopy = sourceCopy.replace(killStrings[i],"").trim();
            }
        }
        //logger.info("killedStrings, source: '"+sourceString+"' killed: '"+sourceCopy+"'");
        return sourceCopy;
    }

    public static String[] scrub(String[] sourceStrings) {
        if (sourceStrings == null){ 
            return null; 
        }

        int count = sourceStrings.length;

        String[] cleanedResults = new String[count];
        for (int i=0; i<count; i++) {
            cleanedResults[i] = scrub(sourceStrings[i]);
        }

        return cleanedResults;
    }
    
    public static Map<String, Object> killParameterMap(Map<String, Object> paramMap) {
        Map<String, Object> cleanedMap = new HashMap<String, Object>(paramMap.size());

        for (String key: paramMap.keySet()) {
            if (paramMap.get(key) instanceof String)
                cleanedMap.put(key, KillStringInputScrubber.scrub((String)paramMap.get(key)));
            else if (paramMap.get(key) instanceof String[])
                cleanedMap.put(key, KillStringInputScrubber.scrub((String[])paramMap.get(key)));
        }
        return cleanedMap;
    }    
    
}
